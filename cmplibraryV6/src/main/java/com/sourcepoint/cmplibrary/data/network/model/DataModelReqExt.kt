package com.sourcepoint.cmplibrary.data.network.model

import org.json.JSONObject

internal fun UnifiedMessageRequest.toBodyRequest(): String {
    return toJsonObject().toString()
}

internal fun MessageReq.toBodyRequest(): String {
    return toJsonObject().toString()
}

internal fun UnifiedMessageRequest.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("requestUUID", requestUUID)
            put("propertyHref", propertyHref)
            put("accountId", accountId)
            put("idfaStatus", idfaStatus)
            put("campaigns", campaigns.toJsonObject())
            put("consentLanguage", consentLanguage.value)
            put("campaignEnv", campaignEnv)
            put("includeData", includeData.toJsonObject())
        }
}

internal fun MessageReq.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("requestUUID", requestUUID)
            put("campaigns", campaigns.toJsonObject())
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
            put("accountId", accountId)
            put("propertyHref", propertyHref)
//            put("propertyId", propertyId)
            put("targetingParams", targetingParams)
            put("meta", meta)
            put("uuid", uuid)
        }
}

internal fun TargetingParams.toJsonObjStringify(): String {
    return JSONObject()
        .apply {
            put("legislation", legislation)
            put("location", location)
        }
        .toString()
}

internal fun IncludeData.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("actions", JSONObject().apply { put("type", actions.type) })
            put("cookies", JSONObject().apply { put("type", cookies.type) })
            put("customVendorsResponse", JSONObject().apply { put("type", customVendorsResponse.type) })
            put("localState", JSONObject().apply { put("type", localState.type) })
        }
}
