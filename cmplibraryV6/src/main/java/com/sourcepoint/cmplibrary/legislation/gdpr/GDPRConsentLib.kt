package com.sourcepoint.cmplibrary.legislation.gdpr

import android.app.Activity
import android.view.View
import com.sourcepoint.cmplibrary.ConsentLib

interface GDPRConsentLib : ConsentLib {
    var spGdprClient: SpGDPRClient?
    // set the client for implementing the callback
//    fun setClient(/*gdpr : Client*/)
    fun showView(view : View)
}
