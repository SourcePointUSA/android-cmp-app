package com.sourcepoint.cmplibrary.data.network.model

data class UserConsent(
    var acceptedCategories: List<Any> = listOf(),
    var acceptedVendors: List<Any> = listOf(),
    var euconsent: String = "",
    var specialFeatures: List<Any> = listOf()
)
