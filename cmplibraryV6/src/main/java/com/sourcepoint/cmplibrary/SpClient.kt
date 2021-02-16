package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.SPConsents

interface SpClient {
    /**
     * It is invoked when the WebView has been already loaded with all the consent Info
     */
    fun onUIReady(view: View)

    /**
     * It is invoked when the message is available to the client App
     */
    fun onMessageReady(message: SPMessage)
    fun onAction(view: View, actionType: ActionType)
    fun onUIFinished(view: View)
    fun onConsentReady(consent: SPConsents)
    fun onError(error: Throwable)
}

class SPMessage
