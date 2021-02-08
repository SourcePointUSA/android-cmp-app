package com.sourcepoint.cmplibrary.data.network.model

import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.gdpr_cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.gdpr_cmplibrary.exception.Legislation
import com.sourcepoint.gdpr_cmplibrary.exception.Legislation.*  //ktlint-disable
import org.json.JSONObject
import java.util.* //ktlint-disable

/**
 * ================================== Unified wrapper =======================================
 */

data class MessageResp(
    val legislation: Legislation,
    val message: JSONObject,
    val uuid: String,
    val meta: String
//    val gdpr: Gdpr? = null,
//    val ccpa: Ccpa? = null
)

data class Gdpr(
    val uuid: String,
    val GDPRUserConsent: GDPRUserConsent,
    val meta: String,
    val message: JSONObject
)

data class MessageGdprResp(
    val categories: String,
    val language: String,
    val message_choice: String,
    val message_json: String,
    val site_id: String
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

internal fun String.getAppliedLegislation(): Legislation {
    return when (this.toLowerCase(Locale.getDefault())) {
        "gdpr" -> GDPR
        "ccpa" -> CCPA
        else -> throw InvalidResponseWebMessageException(description = "Invalid Legislation type")
    }
}

/**
 * ===================================== Native Message ====================================
 */

data class NativeMessageResp(
    val msgJSON: JSONObject
)
