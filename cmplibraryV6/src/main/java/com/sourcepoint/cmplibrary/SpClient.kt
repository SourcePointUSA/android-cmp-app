package com.sourcepoint.cmplibrary

import android.view.View
import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import com.sourcepoint.gdpr_cmplibrary.ConsentLibException

interface SpClient {
    fun onConsentUIFinished(v: View)
    fun onConsentUIReady(v: View)
    fun onError(error: ConsentLibException?)
    fun onAction(actionTypes: ActionTypes?)
}
