package com.sourcepoint.app.v6

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.sourcepoint.app.v6.databinding.NativeMessageBinding
import com.sourcepoint.app.v6.databinding.OnlyGdprBinding
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
import org.json.JSONObject

class NativeMessageActivity : AppCompatActivity() {

    private val spConsentLib by spConsentLibLazy {
        activity = this@NativeMessageActivity
        spClient = LocalClient()
        config {
            accountId = 22
            propertyId = 16893
            propertyName = "mobile.multicampaign.native.demo2"
            messLanguage = MessageLanguage.ENGLISH
            +(CampaignType.GDPR)
        }
    }

    private lateinit var onlyGdprBinding: OnlyGdprBinding
    private lateinit var nativeMessageBinding: NativeMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onlyGdprBinding = OnlyGdprBinding.inflate(layoutInflater)
        setContentView(onlyGdprBinding.root)

        onlyGdprBinding.reviewConsentsGdpr.setOnClickListener {
            spConsentLib.loadPrivacyManager(
                "548285",
                PMTab.PURPOSES,
                CampaignType.GDPR
            )
        }

        onlyGdprBinding.clearAll.setOnClickListener { clearAllData(this) }
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
            Log.i(this::class.java.name, "onNativeMessageReady: $message")
            setNativeMessage(message, messageController)
        }

        override fun onUIFinished(view: View) {
            spConsentLib.removeView(view)
            Log.i(this::class.java.name, "onUIFinished")
        }

        override fun onError(error: Throwable) {
            Log.i(this::class.java.name, "onError: ${error.message}")
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
        nativeMessageBinding = NativeMessageBinding.inflate(layoutInflater, null, false)
        val nativeMessageRootView = nativeMessageBinding.root
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
                    removeNativeView(nativeMessageRootView)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.ACCEPT_ALL, message.campaignType)
                }
            }
            cancel.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageRootView)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.MSG_CANCEL, message.campaignType)
                }
            }
            rejectAll.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageRootView)
                    /** ... update the consent in the BE. */
                    sendConsent(NativeMessageActionType.REJECT_ALL, message.campaignType)
                }
            }
            showOptionsBtn.setOnClickListener {
                messageController.run {
                    /** After the click action, you need to remove the current layout and ... */
                    removeNativeView(nativeMessageRootView)
                    /** ... layout the Privacy Manager. */
                    messageController.showOptionNativeMessage(message.campaignType, "548285")
                }
            }
        }
        /** 4. Display the layout */
        messageController.showNativeView(nativeMessageRootView)
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
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    private fun setOptionBtn(na: NativeAction) {
        nativeMessageBinding.showOptionsBtn.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    private fun setRejectAllBtn(na: NativeAction) {
        nativeMessageBinding.rejectAll.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }

    private fun setAcceptAllBtn(na: NativeAction) {
        nativeMessageBinding.acceptAll.run {
            text = na.text
            setBackgroundColor(na.style.backgroundColor.toColorInt() ?: throw RuntimeException())
            setTextColor(na.style.color?.toColorInt() ?: throw RuntimeException())
            textSize = na.style.fontSize ?: 10F
        }
    }
}