package com.sourcepoint.cmplibrary.gdpr

import com.sourcepoint.cmplibrary.ConsentLib

interface GDPRConsentLibClient : ConsentLib {
    val clientInteraction: ClientInteraction
    // set the client for implementing the callback
//    fun setClient(/*gdpr : Client*/)
}