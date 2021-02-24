package com.sourcepoint.cmplibrary.data.network.model

import org.json.JSONObject

internal fun MessageReq.toBodyRequest(): String {
    return toJsonObject().toString()
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
            put("propertyId", propertyId)
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
