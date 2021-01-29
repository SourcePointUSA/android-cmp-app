package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.impl.DeferredMap

data class MessageResp(
    val gdpr: Gdpr? = null,
    val ccpa: Ccpa? = null
)

data class Gdpr(
    val uuid: String,
    val GDPRUserConsent: GDPRUserConsent,
    val meta: String,
    val message: String
)

data class Ccpa(
    val uuid: String,
    val message: String
)

data class GDPRUserConsent(
    var acceptedCategories: List<Any> = listOf(),
    var acceptedVendors: List<Any> = listOf(),
    var specialFeatures: List<Any> = listOf(),
    var legIntCategories: List<Any> = listOf(),
    var euconsent: String = "",
    var tcData: DeferredMap = DeferredMap(false),
    var vendorsGrants: DeferredMap = DeferredMap(false),
)
