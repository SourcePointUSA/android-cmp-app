package com.sourcepoint.cmplibrary.data.network.model.consent

import org.json.JSONObject

data class ConsentReq(
    val uuid: String,
    val choiceId: String,
    val consentLanguage: String,
    val meta: String,
    val propertyHref: String,
    val privacyManagerId: String,
    val requestUUID: String,
    val accountId: Int,
    val actionType: Int,
    val requestFromPM: Boolean,
    val pubData: JSONObject = JSONObject(),
    val pmSaveAndExitVariables: JSONObject = JSONObject()
)

fun ConsentReq.toBodyRequest(): String {
    return JSONObject()
        .apply {
            put("uuid", uuid)
            put("choiceId", choiceId)
            put("consentLanguage", consentLanguage)
            put("meta", meta)
            put("propertyHref", propertyHref)
            put("privacyManagerId", privacyManagerId)
            put("requestUUID", requestUUID)
            put("accountId", accountId)
            put("actionType", actionType)
            put("requestFromPM", requestFromPM)
            put("pubData", pubData)
            put("pmSaveAndExitVariables", pmSaveAndExitVariables)
        }
        .toString()
//    return JSON.std.asString(this)
}
