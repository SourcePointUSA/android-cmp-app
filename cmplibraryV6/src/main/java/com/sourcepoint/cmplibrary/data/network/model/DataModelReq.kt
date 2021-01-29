package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.JSON

data class MessageReq(
    val categories: Categories,
    val requestUUID: String
)

data class Categories(
    val gdpr: GdprReq
)

data class GdprReq(
    val accountId: Int,
    val propertyHref: String,
    val propertyId: Int
)

fun MessageReq.toBodyRequest(): String {
    return JSON.std.asString(this)
}
