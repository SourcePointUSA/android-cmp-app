package com.sourcepoint.app.v6

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.core.nativemessage.NativeAction
import com.sourcepoint.cmplibrary.core.nativemessage.NativeComponent
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import kotlinx.android.synthetic.main.native_message.view.*
import org.json.JSONObject
import org.koin.android.ext.android.inject

class NativeMessageActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "**NativeMessageActivity"
    }

    private val dataProvider by inject<DataProvider>()
    private val spClientObserver: List<SpClient> by inject()

    private val spConsentLib by spConsentLibLazy {
        activity = this@NativeMessageActivity
        spClient = LocalClient()
        spConfig = dataProvider.spConfig
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (dataProvider.resetAll) {
            clearAllData(this)
        }
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.review_consents_gdpr).setOnClickListener { _v: View? ->
            if (dataProvider.isOtt) {
                spConsentLib.loadOTTPrivacyManager(
                    dataProvider.gdprPmId,
                    CampaignType.GDPR
                )
            } else {
                spConsentLib.loadPrivacyManager(
                    dataProvider.gdprPmId,
                    PMTab.PURPOSES,
                    CampaignType.GDPR,
                )
            }
        }
        findViewById<View>(R.id.review_consents_ccpa).setOnClickListener { _v: View? ->
            if (dataProvider.isOtt) {
                spConsentLib.loadOTTPrivacyManager(
                    dataProvider.ccpaPmId,
                    CampaignType.CCPA
                )
            } else {
                spConsentLib.loadPrivacyManager(
                    dataProvider.ccpaPmId,
                    PMTab.PURPOSES,
                    CampaignType.CCPA,
                )
            }

        }
        findViewById<View>(R.id.clear_all).setOnClickListener { _v: View? -> clearAllData(this) }
        findViewById<View>(R.id.auth_id_activity).setOnClickListener { _v: View? ->
            startActivity(Intent(this, MainActivityAuthId::class.java))
        }
        findViewById<View>(R.id.custom_consent).setOnClickListener { _v: View? ->
            spConsentLib.customConsentGDPR(
                vendors = dataProvider.customVendorList,
                categories = dataProvider.customCategories,
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
        }

    }

    override fun onResume() {
        super.onResume()
        dataProvider.authId
            ?.let { spConsentLib.loadMessage(it) }
            ?: kotlin.run { spConsentLib.loadMessage() }

    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {

        override fun onMessageReady(message: JSONObject) {}

        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) {
            spClientObserver.forEach { it.onNativeMessageReady(message, messageController) }
            setNativeMessage(message, messageController)
        }

        override fun onError(error: Throwable) {
            spClientObserver.forEach { it.onError(error) }
            error.printStackTrace()
            Log.i(NativeMessageActivity.TAG, "onError: $error")
        }

        override fun onConsentReady(consent: SPConsents) {
            val grants = consent.gdpr?.consent?.grants
            grants?.forEach { grant ->
                val granted = grants[grant.key]?.granted
                val purposes = grants[grant.key]?.purposeGrants
                println("vendor: ${grant.key} - granted: $granted - purposes: $purposes")
            }
            spClientObserver.forEach { it.onConsentReady(consent) }
            Log.i(NativeMessageActivity.TAG, "onConsentReady: $consent")
        }

        override fun onUIFinished(view: View) {
            spClientObserver.forEach { it.onUIFinished(view) }
            spConsentLib.removeView(view)
            Log.i(NativeMessageActivity.TAG, "onUIFinished")
        }

        override fun onUIReady(view: View) {
            spClientObserver.forEach { it.onUIReady(view) }
            spConsentLib.showView(view)
            Log.i(NativeMessageActivity.TAG, "onUIReady")
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            spClientObserver.forEach { it.onAction(view, consentAction) }
            Log.i(NativeMessageActivity.TAG, "onAction ActionType: $consentAction")
            consentAction.pubData.put("pb_key", "pb_value")
            return consentAction
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            spClientObserver.forEach { it.onSpFinished(sPConsents) }
            Log.i(NativeMessageActivity.TAG, "onSpFinish: $sPConsents")
            Log.i(NativeMessageActivity.TAG, "==================== onSpFinish ==================")
        }

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(NativeMessageActivity.TAG, "onNoIntentActivitiesFound: $url")
            spClientObserver.forEach { it.onNoIntentActivitiesFound(url) }
        }
    }

    fun setNativeMessage(message: MessageStructure, messageController: NativeMessageController) {
        /** 1. Create a native layout */
        val customLayout = View.inflate(this, R.layout.native_message, null)
        customLayout.run {
            /** 2. Apply the style to the buttons */
            message.messageComponents?.let {
                setTitle(customLayout, it.title ?: throw RuntimeException())
                setBody(customLayout, it.body ?: throw RuntimeException())
                setAgreeBtn(customLayout, it.body ?: throw RuntimeException())
                it.actions.forEach { a ->
                    when (a.choiceType) {
                        NativeMessageActionType.REJECT_ALL -> setRejectAllBtn(customLayout, a)
                        NativeMessageActionType.ACCEPT_ALL -> setAcceptAllBtn(customLayout, a)
                        NativeMessageActionType.MSG_CANCEL -> setCancelBtn(customLayout, a)
                        NativeMessageActionType.SHOW_OPTIONS -> setOptionBtn(customLayout, a)
                    }
                }
            }
            /** 3. Associate the button to the related action */
            accept_all.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(customLayout)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.ACCEPT_ALL, message.campaignType)
                }
            }
            cancel.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(customLayout)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.MSG_CANCEL, message.campaignType)
                }
            }
            reject_all.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(customLayout)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.REJECT_ALL, message.campaignType)
                }
            }
            show_options_btn.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(customLayout)
                    /** ... layout the Privacy Manager. */
                    messageController.showOptionNativeMessage(message.campaignType, "548285")
                }
            }
        }
        /** 4. Display the layout */
        messageController.showNativeView(customLayout)
    }

    private fun setTitle(view: View, t: NativeComponent) {
        view.title_nm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
            textSize = t.style?.fontSize ?: 10F
        }
    }

    private fun setBody(view: View, t: NativeComponent) {
        view.body_nm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
            textSize = t.style?.fontSize ?: 10F
            movementMethod = ScrollingMovementMethod()
        }
    }

    private fun setAgreeBtn(view: View, t: NativeComponent) {
        view.body_nm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
        }
    }

    private fun setCancelBtn(view: View, na: NativeAction) {
        view.cancel.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    private fun setOptionBtn(view: View, na: NativeAction) {
        view.show_options_btn.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    private fun setRejectAllBtn(view: View, na: NativeAction) {
        view.reject_all.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    private fun setAcceptAllBtn(view: View, na: NativeAction) {
        view.accept_all.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }
}