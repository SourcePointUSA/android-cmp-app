package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.impl.DeferredMap

sealed class UserConsent

data class GDPRUserConsent(
    var acceptedCategories: List<Any> = listOf(),
    var acceptedVendors: List<Any> = listOf(),
    var specialFeatures: List<Any> = listOf(),
    var legIntCategories: List<Any> = listOf(),
    var euconsent: String = "",
    var tcData: DeferredMap = DeferredMap(false),
    var vendorsGrants: DeferredMap = DeferredMap(false)
) : UserConsent()

data class CCPAUserConsent(
    var status: CCPAStatus = CCPAStatus.REJECTED_NONE,
    var rejectedVendors: List<Any> = listOf(),
    var rejectedCategories: List<Any> = listOf(),
    var uspstring: String = ""
) : UserConsent()

enum class CCPAStatus(val state: String) {
    REJECTED_NONE("rejectedNone"),
    REJECTED_SOME("rejectedSome"),
    REJECTED_ALL("rejectedAll"),
    CONSENTED_ALL("consentedAll")
}
