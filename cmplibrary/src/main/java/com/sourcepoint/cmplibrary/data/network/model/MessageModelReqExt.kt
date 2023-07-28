package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import org.json.JSONObject

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
