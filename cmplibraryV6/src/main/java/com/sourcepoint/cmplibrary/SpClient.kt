package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.legislation.ccpa.CCPAUserConsent
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent

interface SpClient {
    fun onConsentUIFinished(v: View)
    fun onConsentUIReady(v: View)
    fun onError(error: ConsentLibException?)
    fun onAction(actionTypes: ActionTypes)
    fun onConsentReadyCallback(c: CCPAUserConsent)
    fun onConsentReady(c: GDPRUserConsent)
}
