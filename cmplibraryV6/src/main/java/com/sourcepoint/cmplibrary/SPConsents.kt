package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.model.SPCCPAConsents
import com.sourcepoint.cmplibrary.data.network.model.SPGDPRConsent

data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsents? = null
)
