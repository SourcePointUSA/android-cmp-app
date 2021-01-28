package com.sourcepoint.cmplibrary.legislation.ccpa

import com.sourcepoint.cmplibrary.ClientInteraction

interface CCPAClientInteraction : ClientInteraction {
    fun onConsentReadyCallback(c: CCPAUserConsent?)
}
