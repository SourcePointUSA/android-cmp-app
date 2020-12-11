package com.sourcepoint.cmplibrary.gdpr

import android.view.View
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent

interface ClientInteraction {
    fun onConsentUIFinishedCallback(v: View)
    fun onConsentUIReadyCallback(v: View)
    fun onErrorCallback(error: ConsentLibException?)
    fun onActionCallback(actionTypes: ActionTypes?)
    fun onConsentReadyCallback(c: GDPRUserConsent?)
}