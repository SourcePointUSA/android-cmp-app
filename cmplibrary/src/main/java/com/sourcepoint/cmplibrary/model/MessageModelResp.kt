package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import okhttp3.HttpUrl
import org.json.JSONObject
import java.util.* // ktlint-disable

/**
 * ================================== Unified wrapper =======================================
 */

internal data class UnifiedMessageResp(
    val thisContent: JSONObject,
    val propertyPriorityData: JSONObject = JSONObject(),
    val campaigns: List<CampaignResp> = emptyList(),
    val localState: String = "",
)

internal abstract class CampaignResp {
    abstract val thisContent: JSONObject
    abstract val type: String
    abstract val url: HttpUrl?
    abstract val applies: Boolean
    abstract val message: JSONObject?
    abstract val messageMetaData: JSONObject?
}

internal data class Gdpr(
    override val thisContent: JSONObject,
    override val applies: Boolean = false,
    override val message: JSONObject? = null,
    override val messageMetaData: JSONObject? = null,
    override val url: HttpUrl?,
    val userConsent: GDPRConsent,
    override val type: String = CampaignType.GDPR.name,
) : CampaignResp()

internal data class Ccpa(
    override val thisContent: JSONObject,
    override val applies: Boolean = false,
    override val message: JSONObject? = null,
    override val messageMetaData: JSONObject? = null,
    override val url: HttpUrl?,
    val userConsent: CCPAConsent,
    override val type: String = CampaignType.CCPA.name,
) : CampaignResp()

internal data class GDPRConsent(
    var euConsent: String = "",
    var tcData: Map<String, Any?> = emptyMap(),
    var vendorsGrants: Map<String, Any?> = emptyMap(),
    val thisContent: JSONObject = JSONObject()
)

internal fun String.getAppliedLegislation(): CampaignType {
    return when (this.toLowerCase(Locale.getDefault())) {
        "gdpr" -> CampaignType.GDPR
        "ccpa" -> CampaignType.CCPA
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
