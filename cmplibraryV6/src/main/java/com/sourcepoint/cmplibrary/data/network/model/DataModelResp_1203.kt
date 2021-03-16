package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

class UnifiedMessageResp1230(
    val list: List<CampaignResp1203> = emptyList()
)

sealed class CampaignResp1203(
    val thisContent: JSONObject = JSONObject(),
    val type: String? = null,
    val applies: Boolean = false,
    val message: JSONObject? = null,
    val userConsent: JSONObject? = null
)

class Gdpr1203(
    thisContent: JSONObject,
    applies: Boolean = false,
    message: JSONObject? = null,
    userConsent: JSONObject? = null
) : CampaignResp1203(thisContent, Legislation.GDPR.name, applies, message, userConsent)

class Ccpa1203(
    thisContent: JSONObject,
    applies: Boolean = false,
    message: JSONObject? = null,
    userConsent: JSONObject? = null
) : CampaignResp1203(thisContent, Legislation.CCPA.name, applies, message, userConsent)
