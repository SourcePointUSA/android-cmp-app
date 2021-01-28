package com.sourcepoint.cmplibrary.legislation.ccpa

import com.sourcepoint.cmplibrary.SpClient

interface SpCCPAClient : SpClient {
    fun onConsentReadyCallback(c: CCPAUserConsent?)
}
