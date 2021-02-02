package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.legislation.gdpr.PrivacyManagerTabK

data class PmUrlConfig(
    val pmTab: PrivacyManagerTabK = PrivacyManagerTabK.PURPOSES,
    val consentLanguage: String = "",
    val consentUUID: String,
    val siteId: String,
    val messageId: String
)
