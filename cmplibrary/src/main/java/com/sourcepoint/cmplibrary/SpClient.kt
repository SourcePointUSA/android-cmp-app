package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.core.nativemessage.MessageStructure
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import org.json.JSONObject

interface SpClient {

    /**
     * It is invoked when the WebView has been already loaded with all the consent Info
     */
    fun onUIReady(view: View)

    /**
     * It is invoked when the message is available to the client App
     */
    fun onNativeMessageReady(message: MessageStructure, messageController: NativeMessageController)

    @Deprecated("onMessageReady callback will be removed in favor of onMessageReady(message: MessageStructure, messageController: NativeMessageController). Currently this callback is disabled.")
    fun onMessageReady(message: JSONObject)
    fun onAction(view: View, consentAction: ConsentAction): ConsentAction

    fun onUIFinished(view: View)
    fun onConsentReady(consent: SPConsents)
    fun onError(error: Throwable)

    /**
     * It is invoked to signaling that all the campaigns in the SDK get processed.
     */
    fun onSpFinished(sPConsents: SPConsents)

    /**
     * This callback is invoked if no activity could open an intent with the given url.
     */
    fun onNoIntentActivitiesFound(url: String)
}

interface UnitySpClient : SpClient {
    fun onConsentReady(consent: String)
    fun onSpFinished(consent: String)
}
