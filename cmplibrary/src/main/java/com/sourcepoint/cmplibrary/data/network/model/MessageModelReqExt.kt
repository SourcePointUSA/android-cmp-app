package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.model.* // ktlint-disable
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
            put("pubData", pubData)
            put("campaignEnv", campaignsEnv.env)
            put("campaigns", campaigns.toJsonObject())
            put("consentLanguage", consentLanguage.value)
            put("localState", localState)
            put("authId", authId)
            put("includeData", includeData.toJsonObject())
        }
}

internal fun Campaigns.toJsonObject(): JSONObject {

    return JSONObject().also { cm ->
        list.map {
            cm.put(
                it.campaignType.name.lowercase(),
                JSONObject().apply { put("targetingParams", it.targetingParams.toJsonObjStringify()) }
            )
        }
    }
}

internal fun List<TargetingParam>.toJsonObjStringify(): JSONObject {
    return JSONObject()
        .apply {
            this@toJsonObjStringify.forEach {
                put(it.key, it.value)
            }
        }
}

internal fun IncludeData.toJsonObject(): JSONObject {
    return JSONObject()
        .apply {
            put("messageMetaData", JSONObject().apply { put("type", messageMetaData.type) })
            put("TCData", JSONObject().apply { put("type", tCData.type) })
            put("localState", JSONObject().apply { put("type", localState.type) })
            put("customVendorsResponse", JSONObject().apply { put("type", customVendorsResponse.type) })
        }
}
