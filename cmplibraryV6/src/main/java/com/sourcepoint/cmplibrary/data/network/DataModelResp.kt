package com.sourcepoint.cmplibrary.data.network

data class UWResp(
    val gdpr: Gdpr?
)

data class Gdpr(
    val message: String,
    val meta: String,
    val uuid: String,
    val userConsent: UserConsent?=null
)