package com.sourcepoint.cmplibrary.data.network.model.consent

import com.fasterxml.jackson.jr.ob.JSON
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
    val propertyId: Int,
    val requestFromPM: Boolean,
    val pubData: JSONObject = JSONObject(),
    val pmSaveAndExitVariables: JSONObject = JSONObject()
)

fun ConsentReq.toBodyRequest(): String {
    return JSON.std.asString(this)
}
