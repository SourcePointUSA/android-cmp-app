package com.sourcepoint.cmplibrary.data.network

data class UserConsent(
    val acceptedCategories: List<Any>,
    val acceptedVendors: List<Any>,
    val euconsent: String,
    val specialFeatures: List<Any>
)