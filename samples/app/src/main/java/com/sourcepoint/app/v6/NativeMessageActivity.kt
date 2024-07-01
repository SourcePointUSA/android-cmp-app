package com.sourcepoint.app.v6

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.databinding.ActivityMainV7Binding
import com.sourcepoint.app.v6.databinding.NativeMessageBinding
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

    private lateinit var binding: ActivityMainV7Binding
    private lateinit var nativeMessageBinding: NativeMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (dataProvider.resetAll) {
            clearAllData(this)
        }

        binding = ActivityMainV7Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reviewConsentsGdpr.setOnClickListener {
            spConsentLib.loadPrivacyManager(
                dataProvider.gdprPmId,
                PMTab.PURPOSES,
                CampaignType.GDPR,
            )
        }
        binding.reviewConsentsCcpa.setOnClickListener {
            spConsentLib.loadPrivacyManager(
                dataProvider.ccpaPmId,
                PMTab.PURPOSES,
                CampaignType.CCPA,
            )
        }
        binding.clearAll.setOnClickListener { clearAllData(this) }
        binding.authIdActivity.setOnClickListener {
            startActivity(Intent(this, MainActivityAuthId::class.java))
        }
        binding.customConsent.setOnClickListener {
            spConsentLib.customConsentGDPR(
                vendors = dataProvider.customVendorList,
                categories = dataProvider.customCategories,
                legIntCategories = emptyList(),
                success = { spCustomConsents -> println("custom consent: [$spCustomConsents]") }
            )
        }
        binding.refreshBtn.setOnClickListener { executeCmpLib() }
    }

    override fun onResume() {
        super.onResume()
        executeCmpLib()
    }

    private fun executeCmpLib() {
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
            Log.i(TAG, "onError: $error")
        }

        override fun onConsentReady(consent: SPConsents) {
            val grants = consent.gdpr?.consent?.grants
            grants?.forEach { grant ->
                val granted = grants[grant.key]?.granted
                val purposes = grants[grant.key]?.purposeGrants
                println("vendor: ${grant.key} - granted: $granted - purposes: $purposes")
            }
            spClientObserver.forEach { it.onConsentReady(consent) }
            Log.i(TAG, "onConsentReady: $consent")
        }

        override fun onUIFinished(view: View) {
            spClientObserver.forEach { it.onUIFinished(view) }
            spConsentLib.removeView(view)
            Log.i(TAG, "onUIFinished")
        }

        override fun onUIReady(view: View) {
            spClientObserver.forEach { it.onUIReady(view) }
            spConsentLib.showView(view)
            Log.i(TAG, "onUIReady")
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            spClientObserver.forEach { it.onAction(view, consentAction) }
            Log.i(TAG, "onAction ActionType: $consentAction")
            consentAction.pubData.put("pb_key", "pb_value")
            return consentAction
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            spClientObserver.forEach { it.onSpFinished(sPConsents) }
            Log.i(TAG, "onSpFinish: $sPConsents")
            Log.i(TAG, "==================== onSpFinish ==================")
        }

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(TAG, "onNoIntentActivitiesFound: $url")
            spClientObserver.forEach { it.onNoIntentActivitiesFound(url) }
        }
    }

    fun setNativeMessage(message: MessageStructure, messageController: NativeMessageController) {
        /** 1. Create a native layout */
        nativeMessageBinding = NativeMessageBinding.inflate(layoutInflater, null, false)
        nativeMessageBinding.run {
            /** 2. Apply the style to the buttons */
            message.messageComponents?.let {
                setTitle(it.title ?: throw RuntimeException())
                setBody(it.body ?: throw RuntimeException())
                setAgreeBtn(it.body ?: throw RuntimeException())
                it.actions.forEach { a ->
                    when (a.choiceType) {
                        NativeMessageActionType.REJECT_ALL -> setRejectAllBtn(a)
                        NativeMessageActionType.ACCEPT_ALL -> setAcceptAllBtn(a)
                        NativeMessageActionType.MSG_CANCEL -> setCancelBtn(a)
                        NativeMessageActionType.SHOW_OPTIONS -> setOptionBtn(a)
                        else -> {}
                    }
                }
            }
            /** 3. Associate the button to the related action */
            acceptAll.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageBinding.root)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.ACCEPT_ALL, message.campaignType)
                }
            }
            cancel.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageBinding.root)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.MSG_CANCEL, message.campaignType)
                }
            }
            rejectAll.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageBinding.root)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.REJECT_ALL, message.campaignType)
                }
            }
            showOptionsBtn.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageBinding.root)
                    /** ... layout the Privacy Manager. */
                    messageController.showOptionNativeMessage(message.campaignType, "548285")
                }
            }
        }
        /** 4. Display the layout */
        messageController.showNativeView(nativeMessageBinding.root)
    }

    private fun setTitle(t: NativeComponent) {
        nativeMessageBinding.titleNm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
            textSize = t.style?.fontSize ?: 10F
        }
    }

    private fun setBody(t: NativeComponent) {
        nativeMessageBinding.bodyNm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
            textSize = t.style?.fontSize ?: 10F
            movementMethod = ScrollingMovementMethod()
        }
    }

    private fun setAgreeBtn(t: NativeComponent) {
        nativeMessageBinding.bodyNm.run {
            text = t.text ?: ""
            setBackgroundColor(t.style?.backgroundColor?.toColorInt() ?: throw RuntimeException())
            setTextColor(t.style?.color?.toColorInt() ?: throw RuntimeException())
        }
    }

    private fun setCancelBtn(na: NativeAction) {
        nativeMessageBinding.cancel.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize
        }
    }

    private fun setOptionBtn(na: NativeAction) {
        nativeMessageBinding.showOptionsBtn.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize
        }
    }

    private fun setRejectAllBtn(na: NativeAction) {
        nativeMessageBinding.rejectAll.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize
        }
    }

    private fun setAcceptAllBtn(na: NativeAction) {
        nativeMessageBinding.acceptAll.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize
        }
    }
}