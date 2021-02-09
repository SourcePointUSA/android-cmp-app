package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.cmplibrary.data.network.model.CCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.model.GDPRUserConsent
import com.sourcepoint.cmplibrary.exception.ConsentLibExceptionK
import com.sourcepoint.cmplibrary.model.ActionType

interface SpClient {
    fun onConsentUIFinished(v: View)
    fun onConsentUIReady(v: View)
    fun onError(error: ConsentLibExceptionK)
    fun onAction(actionTypes: ActionType)
    fun onConsentReadyCallback(c: CCPAUserConsent)
    fun onConsentReady(c: GDPRUserConsent)
}
