package com.sourcepoint.cmplibrary.data.network.model

import org.json.JSONObject

fun Gdpr.toJsonObj(): JSONObject {
    return JSONObject()
        .apply {
            put("uuid", uuid)
            put("meta", meta)
            put("message", message)
            put("gdprApplies", gdprApplies)
            put("userConsent", userConsent?.thisContent)
        }
}
