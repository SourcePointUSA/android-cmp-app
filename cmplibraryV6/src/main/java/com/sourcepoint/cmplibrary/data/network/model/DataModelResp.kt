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
data class UnifiedMessageResp(
    val campaigns : List<CampaignResp>
)

data class MessageResp(
    val legislation: Legislation,
    val message: JSONObject,
    val uuid: String,
    val meta: String,
    val userConsent: UserConsent
//    val gdpr: Gdpr? = null,
//    val ccpa: Ccpa? = null
)

sealed class CampaignResp(
    val uuid: String,
    val meta: String,
    val message: JSONObject
)

class Gdpr(
    uuid: String,
    meta: String,
    message: JSONObject,
    val gdprUserConsent: GDPRUserConsent
) : CampaignResp(uuid, meta, message)

class Ccpa(
    uuid: String,
    meta: String,
    message: JSONObject,
    val ccpaApplies : Boolean,
    val ccpaUserConsent: CCPAUserConsent
) : CampaignResp(uuid, meta, message)

data class MessageGdprResp(
    val categories: String,
    val language: String,
    val message_choice: String,
    val message_json: String,
    val site_id: String
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
