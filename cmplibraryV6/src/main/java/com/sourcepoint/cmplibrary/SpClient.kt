package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.data.network.model.SPCCPAConsents
import com.sourcepoint.cmplibrary.data.network.model.SPGDPRConsent
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.model.ActionType

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
    fun onConsentReady(consent: SPCCPAConsents)
    fun onConsentReady(consent: SPGDPRConsent)
    fun onError(error: ConsentLibExceptionK)
}

class SPMessage
