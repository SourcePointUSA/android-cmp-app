package com.sourcepoint.cmplibrary.legislation.gdpr

import com.sourcepoint.cmplibrary.ConsentLib

interface GDPRConsentLibClient : ConsentLib {
    var clientInteraction: GDPRClientInteraction?
    // set the client for implementing the callback
//    fun setClient(/*gdpr : Client*/)
}
