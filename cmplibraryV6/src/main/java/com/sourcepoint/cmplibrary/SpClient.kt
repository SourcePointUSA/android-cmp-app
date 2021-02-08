package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.data.network.model.CCPAUserConsent
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent

interface SpClient {
    fun onConsentUIFinished(v: View)
    fun onConsentUIReady(v: View)
    fun onError(error: ConsentLibExceptionK)
    fun onAction(actionTypes: ActionTypes)
    fun onConsentReadyCallback(c: CCPAUserConsent)
    fun onConsentReady(c: GDPRUserConsent)
}
