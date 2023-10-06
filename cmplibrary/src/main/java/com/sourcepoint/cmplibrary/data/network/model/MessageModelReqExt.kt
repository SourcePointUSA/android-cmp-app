package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.Campaigns
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import kotlinx.serialization.json.* // ktlint-disable
import org.json.JSONObject

internal fun Campaigns.toJsonObject(): JSONObject {

    return JSONObject().also { cm ->
        list.map {
            cm.put(
                it.campaignType.name.lowercase(),
                JSONObject().apply {
                    put("targetingParams", it.targetingParams.toJsonObjStringify())
                    put("groupPmId", it.groupPmId)
                }
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
            put("webConsentPayload", JSONObject().apply { put("type", webConsentPayload.type) })
        }
}

internal fun toIncludeDataBodyMess(): JsonObject {
    return buildJsonObject {
        putJsonObject("TCData") {
            put("type", "RecordString")
        }
        putJsonObject("campaigns") {
            put("type", "RecordString")
        }
    }
}
