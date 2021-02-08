package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Legislation.CCPA
import com.sourcepoint.cmplibrary.exception.Legislation.GDPR
import org.json.JSONObject
import java.util.* // ktlint-disable

/**
 * ================================== Unified wrapper =======================================
 */

data class MessageResp(
    val legislation: Legislation,
    val message: JSONObject,
    val uuid: String,
    val meta: String,
    val userConsent: UserConsent
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
