package com.sourcepoint.gdpr_cmplibrary.data.network.model

data class CustomVendorsResponse(
    val consentedPurposes: List<Any>,
    val consentedVendors: List<Any>,
    val legIntPurposes: List<LegIntPurpose>
)