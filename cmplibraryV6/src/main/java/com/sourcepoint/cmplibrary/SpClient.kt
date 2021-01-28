package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException

interface SpClient {
    fun onConsentUIFinishedCallback(v: View)
    fun onConsentUIReadyCallback(v: View)
    fun onErrorCallback(error: ConsentLibException?)
    fun onActionCallback(actionTypes: ActionTypes?)
}
