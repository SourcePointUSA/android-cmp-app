package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
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
    val campaigns: List<CampaignResp> = emptyList(),
    val thisContent: JSONObject? = null
)

sealed class CampaignResp(
    val uuid: String? = null,
    val meta: String? = null,
    val message: JSONObject? = null,
    val thisContent: JSONObject = JSONObject()
)

class Gdpr(
    thisContent: JSONObject,
    uuid: String? = null,
    meta: String? = null,
    message: JSONObject? = null,
    val gdprApplies: Boolean = false,
    val userConsent: GDPRConsent? = null
) : CampaignResp(uuid, meta, message, thisContent)

class Ccpa(
    thisContent: JSONObject,
    uuid: String,
    meta: String,
    message: JSONObject? = null,
    val ccpaApplies: Boolean = false,
    val userConsent: CCPAConsent
) : CampaignResp(uuid, meta, message, thisContent)

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
data class NativeMessageRespK(
    val msg: NativeMessageDto
)
