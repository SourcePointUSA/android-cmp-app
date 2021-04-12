package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.model.PMTab

data class PmUrlConfig(
    val pmTab: PMTab = PMTab.PURPOSES,
    val consentLanguage: String?,
    val consentUUID: String?,
    val siteId: String?,
    val messageId: String?
)
