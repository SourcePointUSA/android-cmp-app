package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.model.SPCCPAConsent
import com.sourcepoint.cmplibrary.model.SPGDPRConsent

data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsent? = null
)
