package com.sourcepoint.cmplibrary.model.ext

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
                it.campaignType.name.toLowerCase(),
                JSONObject()
                    .apply {
                        put("targetingParams", it.targetingParams)
                        put("campaignEnv", it.campaignEnv.value)
                    }
            )
        }
    }
}

internal fun List<TargetingParam>.toJsonObjStringify(): String {
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
            put("messageMetaData", JSONObject().apply { put("type", messageMetaData?.type) })
            put("TCData", JSONObject().apply { put("type", tCData?.type) })
            put("localState", JSONObject().apply { put("type", localState.type) })
        }
}
