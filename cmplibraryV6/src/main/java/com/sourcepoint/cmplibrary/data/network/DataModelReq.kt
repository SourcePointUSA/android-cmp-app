package com.sourcepoint.cmplibrary.data.network

data class UWReq(
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

fun UWReq.toBodyRequest(): String {
    TODO("Not yet implemented")
}