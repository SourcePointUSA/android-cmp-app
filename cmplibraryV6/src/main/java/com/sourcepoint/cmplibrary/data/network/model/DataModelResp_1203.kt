package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

internal data class UnifiedMessageResp1203(
    val thisContent: JSONObject,
    val propertyPriorityData: JSONObject = JSONObject(),
    val campaigns: List<CampaignResp1203> = emptyList(),
    val localState: String = "",
)

internal abstract class CampaignResp1203 {
    abstract val thisContent: JSONObject
    abstract val type: String
    abstract val applies: Boolean
    abstract val message: JSONObject?
    abstract val messageMetaData: JSONObject?
}

internal data class Gdpr1203(
    override val thisContent: JSONObject,
    override val applies: Boolean = false,
    override val message: JSONObject? = null,
    override val messageMetaData: JSONObject? = null,
    override val type: String = Legislation.GDPR.name,
    val userConsent: GDPRConsent1203
) : CampaignResp1203()

internal data class Ccpa1203(
    override val thisContent: JSONObject,
    override val applies: Boolean = false,
    override val message: JSONObject? = null,
    override val messageMetaData: JSONObject? = null,
    override val type: String = Legislation.CCPA.name,
    val userConsent: CCPAConsent
) : CampaignResp1203()

internal data class GDPRConsent1203(
    var euConsent: String = "",
    var tcData: Map<String, Any?> = emptyMap(),
    var vendorsGrants: Map<String, Any?> = emptyMap(),
    val thisContent: JSONObject = JSONObject()
)
