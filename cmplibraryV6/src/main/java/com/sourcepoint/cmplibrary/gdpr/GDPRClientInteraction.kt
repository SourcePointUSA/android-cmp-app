package com.sourcepoint.cmplibrary.gdpr

import com.sourcepoint.cmplibrary.ClientInteraction
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent

interface GDPRClientInteraction : ClientInteraction {
    fun onConsentReadyCallback(c: GDPRUserConsent?)
}