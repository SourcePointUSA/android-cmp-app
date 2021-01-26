package com.sourcepoint.cmplibrary.ccpa

import com.sourcepoint.cmplibrary.ClientInteraction

interface CCPAClientInteraction : ClientInteraction {
    fun onConsentReadyCallback(c: CCPAUserConsent?)
}