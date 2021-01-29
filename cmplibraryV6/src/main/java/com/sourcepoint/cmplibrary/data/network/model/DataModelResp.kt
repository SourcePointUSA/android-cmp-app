package com.sourcepoint.cmplibrary.data.network.model

data class MessageResp(
    val gdpr: Gdpr
)

data class Gdpr(
    val uuid: String,
    val userConsent: UserConsent? = null,
    val meta: String,
    val message: String,
)
