package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.impl.DeferredMap

sealed class SpUserConsent

data class SPGDPRConsent(
    var acceptedCategories: List<Any> = listOf(),
    var acceptedVendors: List<Any> = listOf(),
    var specialFeatures: List<Any> = listOf(),
    var legIntCategories: List<Any> = listOf(),
    var euconsent: String = "",
    var tcData: DeferredMap = DeferredMap(false),
    var vendorsGrants: DeferredMap = DeferredMap(false)
) : SpUserConsent()

data class SPCCPAConsents(
    var status: String? = null,
    var rejectedVendors: List<Any> = listOf(),
    var rejectedCategories: List<Any> = listOf(),
    var uspstring: String = ""
) : SpUserConsent()
