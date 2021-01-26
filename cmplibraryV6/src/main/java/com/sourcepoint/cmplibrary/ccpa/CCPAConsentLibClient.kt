package com.sourcepoint.cmplibrary.ccpa

import com.sourcepoint.cmplibrary.ConsentLib

interface CCPAConsentLibClient : ConsentLib {
    var clientInteraction: CCPAClientInteraction?
    // set the client for implementing the callback
//    fun setClient(/*gdpr : Client*/)
}