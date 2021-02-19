package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.JSON
import com.sourcepoint.cmplibrary.exception.Legislation

internal data class MessageReq(
    val requestUUID: String,
    val campaigns: Campaigns
)

internal data class Campaigns(
    val gdpr: GdprReq? = null,
    val ccpa: CcpaReq? = null
)

internal data class GdprReq(
    val accountId: Int,
    val propertyHref: String,
    val propertyId: Int,
    val targetingParams: String = JSON.std.asString(TargetingParams(Legislation.GDPR.name, "EU"))
)

internal class TargetingParams(
    val legislation: String,
    val location: String
) {
    override fun toString(): String {
        return JSON.std.asString(this)
    }
}

internal data class CcpaReq(
    val accountId: Int,
    val propertyHref: String,
    val propertyId: Int,
    val targetingParams: String = JSON.std.asString(TargetingParams(Legislation.CCPA.name, "US"))
)

internal fun MessageReq.toBodyRequest(): String {
    return JSON.std.asString(this)
}
