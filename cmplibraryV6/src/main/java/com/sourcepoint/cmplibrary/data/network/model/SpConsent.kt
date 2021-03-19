package com.sourcepoint.cmplibrary.data.network.model

import org.json.JSONObject

data class SPGDPRConsent(
    val consent: GDPRConsent,
    val applies: Boolean = false
)
data class SPCCPAConsent(
    val consent: CCPAConsent,
    val applies: Boolean = false
)

data class GDPRConsent(
    var acceptedCategories: List<Any> = listOf(),
    var acceptedVendors: List<Any> = listOf(),
    var specialFeatures: List<Any> = listOf(),
    var legIntCategories: List<Any> = listOf(),
    var euconsent: String = "",
    var tcData: Map<String, Any?> = emptyMap(),
    var vendorsGrants: Map<String, Any?> = emptyMap(),
    val thisContent: JSONObject = JSONObject()
)

data class CCPAConsent(
    val status: String? = null,
    val rejectedVendors: List<Any> = listOf(),
    val rejectedCategories: List<Any> = listOf(),
    val rejectedAll: Boolean,
    val uspstring: String = "",
    val thisContent: JSONObject = JSONObject()
)
