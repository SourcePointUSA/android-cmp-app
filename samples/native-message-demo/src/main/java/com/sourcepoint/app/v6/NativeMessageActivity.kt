package com.sourcepoint.app.v6

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.sourcepoint.cmplibrary.NativeMessageController
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.core.nativemessage.NativeAction
import com.sourcepoint.cmplibrary.core.nativemessage.NativeComponent
import com.sourcepoint.cmplibrary.creation.delegate.spConsentLibLazy
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.util.clearAllData
import kotlinx.android.synthetic.main.native_message.view.*
import kotlinx.android.synthetic.main.only_gdpr.*
import org.json.JSONObject

class NativeMessageActivity : AppCompatActivity() {

    private val spConsentLib by spConsentLibLazy {
        activity = this@NativeMessageActivity
        spClient = LocalClient()
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.native.demo2"
            messLanguage = MessageLanguage.ENGLISH
            messageTimeout = 3000
            +(CampaignType.GDPR)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.only_gdpr)

        review_consents_gdpr.setOnClickListener {
            spConsentLib.loadPrivacyManager(
                "548285",
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }

        clear_all.setOnClickListener { clearAllData(this) }

    }

    override fun onResume() {
        super.onResume()
        spConsentLib.loadMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        spConsentLib.dispose()
    }

    internal inner class LocalClient : SpClient {

        override fun onNoIntentActivitiesFound(url: String) {
            Log.i(this::class.java.name, "onNoIntentActivitiesFound")
        }

        override fun onUIReady(view: View) {
            spConsentLib.showView(view)
            Log.i(this::class.java.name, "onUIReady")
        }

        override fun onAction(view: View, consentAction: ConsentAction): ConsentAction {
            Log.i(this::class.java.name, "ActionType: ${consentAction.actionType}")
            return consentAction
        }

        override fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController) {
            setNativeMessage(message, messageController)
        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
            Log.i(this::class.java.name, "onUIFinished")
        }

        override fun onError(error: Throwable) {
            error.printStackTrace()
        }

        override fun onSpFinished(sPConsents: SPConsents) {
            Log.i(this::class.java.name, "onSpFinish: $sPConsents")
            Log.i(this::class.java.name, "==================== onSpFinish ==================")
        }

        override fun onConsentReady(consent: SPConsents) {
            Log.i(this::class.java.name, "onConsentReady: $consent")
        }

        override fun onMessageReady(message: JSONObject) {}
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