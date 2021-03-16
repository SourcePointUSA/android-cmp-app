package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.CampaignResp1203
import com.sourcepoint.cmplibrary.data.network.model.Ccpa1203
import com.sourcepoint.cmplibrary.data.network.model.Gdpr1203
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp1230
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject

internal fun String.toUnifiedMessageRespDto1203(): UnifiedMessageResp1230 {
    return JSONObject(this).toUnifiedMessageRespDto1203()
}

internal fun JSONObject.toUnifiedMessageRespDto1203(): UnifiedMessageResp1230 {
    val map: Map<String, Any?> = this.toTreeMap()
    val list = map
        .getFieldValue<List<Map<String, Any?>>>("campaigns")
        ?.mapNotNull { it.toCampaignResp1203() }
        ?: emptyList()
    return UnifiedMessageResp1230(list)
}

fun Map<String, Any?>.toCampaignResp1203(): CampaignResp1203? {
    return when (getFieldValue<String>("type")?.toUpperCase()) {
        Legislation.GDPR.name -> Gdpr1203(
            thisContent = JSONObject(this),
            applies = false,
            message = JSONObject(this),
            userConsent = JSONObject(this)
        )
        Legislation.CCPA.name -> Ccpa1203(
            thisContent = JSONObject(this),
            applies = false,
            message = JSONObject(this),
            userConsent = JSONObject(this)
        )
        else -> null
    }
}
