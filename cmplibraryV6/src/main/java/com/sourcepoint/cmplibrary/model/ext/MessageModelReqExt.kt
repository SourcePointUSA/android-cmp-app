package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.CampaignReq
import com.sourcepoint.cmplibrary.model.Campaigns
import com.sourcepoint.cmplibrary.model.UnifiedMessageRequest
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import org.json.JSONObject

internal fun UnifiedMessageRequest.toBodyRequest(): String {
    return toJsonObject().toString()
}

internal fun UnifiedMessageRequest.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("requestUUID", requestUUID)
            put("propertyHref", "http://$propertyHref")
            put("accountId", accountId)
            put("campaigns", campaigns.toJsonObject())
            put("consentLanguage", consentLanguage.value)
            put("localState", localState)
            put("includeData", includeData.toJsonObject())
        }
}

internal fun Campaigns.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("gdpr", gdpr?.toJsonObject())
            put("ccpa", ccpa?.toJsonObject())
        }
}

internal fun CampaignReq.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("targetingParams", targetingParams)
            put("campaignEnv", campaignEnv.value)
        }
}

internal fun Array<TargetingParam>.toJsonObjStringify(): String {
    return JSONObject()
        .apply {
            this@toJsonObjStringify.forEach {
                put(it.key, it.value)
            }
        }
        .toString()
}

internal fun IncludeData.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("messageMetaData", JSONObject().apply { put("type", messageMetaData.type) })
            put("TCData", JSONObject().apply { put("type", tCData.type) })
            put("localState", JSONObject().apply { put("type", localState.type) })
        }
}
