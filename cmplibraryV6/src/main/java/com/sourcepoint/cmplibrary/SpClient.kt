package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import org.json.JSONObject

abstract class SpClient {

    /**
     * It is invoked when the WebView has been already loaded with all the consent Info
     */
    abstract fun onUIReady(view: View)

    /**
     * It is invoked when the message is available to the client App
     */
    abstract fun onMessageReady(message: JSONObject)
    abstract fun onAction(view: View, actionType: ActionType)
    abstract fun onUIFinished(view: View)
    abstract fun onConsentReady(consent: SPConsents)
    open fun onConsentReady(consent: String) {}
    abstract fun onError(error: Throwable)
}
