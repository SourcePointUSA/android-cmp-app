package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.JSON
import com.sourcepoint.cmplibrary.exception.Legislation

data class MessageReq(
    val requestUUID: String,
    val campaigns: Campaigns
)

data class Campaigns(
    val gdpr: GdprReq,
    val ccpa: CcpaReq
)

data class GdprReq(
    val accountId: Int,
    val propertyHref: String,
    val propertyId: Int,
    val targetingParams: String = """{"location": "${Legislation.GDPR.name}"}"""
)

data class Location(val location: String)

data class CcpaReq(
    val accountId: Int,
    val propertyHref: String,
    val propertyId: Int,
    val alwaysDisplayDNS: Boolean = false,
    val targetingParams: String = """{"location": "${Legislation.CCPA.name}"}"""
)

fun MessageReq.toBodyRequest(): String {
    return JSON.std.asString(this)
}
