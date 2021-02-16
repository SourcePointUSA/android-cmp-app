package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.impl.DeferredMap

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
    var tcData: DeferredMap = DeferredMap(false),
    var vendorsGrants: DeferredMap = DeferredMap(false)
)

data class CCPAConsent(
    var status: String? = null,
    var rejectedVendors: List<Any> = listOf(),
    var rejectedCategories: List<Any> = listOf(),
    var uspstring: String = ""
)
