package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
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
    abstract val messageSubCategory: MessageSubCategory?
}

internal data class Gdpr(
    override val thisContent: JSONObject,
    override val url: HttpUrl?,
    val userConsent: GDPRConsentInternal,
    override val messageSubCategory: MessageSubCategory? = null,
    override val applies: Boolean = false,
    override val message: JSONObject? = null,
    override val messageMetaData: JSONObject? = null,
    override val type: String = CampaignType.GDPR.name
) : CampaignResp()

internal data class Ccpa(
    override val thisContent: JSONObject,
    override val url: HttpUrl?,
    val userConsent: CCPAConsentInternal,
    override val messageSubCategory: MessageSubCategory,
    override val applies: Boolean = false,
    override val message: JSONObject? = null,
    override val messageMetaData: JSONObject? = null,
    override val type: String = CampaignType.CCPA.name
) : CampaignResp()

internal fun String.getAppliedLegislation(): CampaignType {
    return when (this.lowercase(Locale.getDefault())) {
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
