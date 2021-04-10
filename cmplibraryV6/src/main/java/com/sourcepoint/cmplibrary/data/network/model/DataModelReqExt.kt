package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.model.TargetingParam
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
            put("targetingParams", targetingParams)
            put("campaignEnv", campaignEnv.name.toLowerCase())
        }
}

// internal fun TargetingParams.toJsonObjStringify(): String {
//    return JSONObject()
//        .apply {
//            put("location", location)
//        }
//        .toString()
// }

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
            put("actions", JSONObject().apply { put("type", actions.type) })
            put("cookies", JSONObject().apply { put("type", cookies.type) })
            put("customVendorsResponse", JSONObject().apply { put("type", customVendorsResponse.type) })
            put("localState", JSONObject().apply { put("type", localState.type) })
        }
}
