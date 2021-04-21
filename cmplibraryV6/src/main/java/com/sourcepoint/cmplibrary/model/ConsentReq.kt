package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.ext.toJsonObject
import org.json.JSONObject

/**
 * REQUEST
 */

internal data class ConsentReq(
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
    val pmSaveAndExitVariables: JSONObject = JSONObject(),
    val includeData: IncludeData = IncludeData(localState = LocalState("string"))
)

internal fun ConsentReq.toBodyRequest(): String {
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
            put("includeData", includeData.toJsonObject())
            put("pmSaveAndExitVariables", pmSaveAndExitVariables)
        }
        .toString()
}

/**
 * RESPONSE
 */

internal data class ConsentResp(
    val content: JSONObject,
    val userConsent: String?,
    val uuid: String,
    val localState: String,
    var legislation: Legislation? = null
)
