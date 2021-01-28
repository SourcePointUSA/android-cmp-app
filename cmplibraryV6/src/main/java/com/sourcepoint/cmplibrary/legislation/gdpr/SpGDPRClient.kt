package com.sourcepoint.cmplibrary.legislation.gdpr

import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent

interface SpGDPRClient : SpClient {
    fun onConsentReady(c: GDPRUserConsent?)
}
