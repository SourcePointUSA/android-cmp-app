package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

data class UnifiedMessageResp1203(
    val list: List<CampaignResp1203> = emptyList()
)

abstract class CampaignResp1203 {
    abstract val thisContent: JSONObject
    abstract val type: String
    abstract val applies: Boolean
    abstract val message: JSONObject
    abstract val messageMetaData: JSONObject
}

data class Gdpr1203(
    override val thisContent: JSONObject,
    override val applies: Boolean = false,
    override val message: JSONObject = JSONObject(),
    override val messageMetaData: JSONObject = JSONObject(),
    override val type: String = Legislation.GDPR.name,
    val userConsent: GDPRConsent1203
) : CampaignResp1203()

data class Ccpa1203(
    override val thisContent: JSONObject,
    override val applies: Boolean = false,
    override val message: JSONObject = JSONObject(),
    override val messageMetaData: JSONObject = JSONObject(),
    override val type: String = Legislation.CCPA.name,
    val userConsent: CCPAConsent
) : CampaignResp1203()

data class GDPRConsent1203(
    var euConsent: String = "",
    var tcData: Map<String, Any?> = emptyMap(),
    var vendorsGrants: Map<String, Any?> = emptyMap(),
    val thisContent: JSONObject = JSONObject()
)
