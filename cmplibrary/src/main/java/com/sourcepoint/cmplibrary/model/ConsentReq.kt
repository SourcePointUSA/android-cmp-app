package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SPCustomConsents
import org.json.JSONArray
import org.json.JSONObject

/**
 * REQUEST
 */

internal class CustomConsentReq(
    val consentUUID: String,
    val propertyId: Int,
    val vendors: List<String>,
    val categories: List<String>,
    val legIntCategories: List<String>,
)

internal fun CustomConsentReq.toBodyRequest(): String {
    return JSONObject()
        .apply {
            put("consentUUID", consentUUID)
            put("propertyId", propertyId)
            put("vendors", JSONArray(vendors))
            put("categories", JSONArray(categories))
            put("legIntCategories", JSONArray(legIntCategories))
        }
        .toString()
}

internal fun CustomConsentReq.toBodyRequestDeleteCustomConsentTo(): String {
    return JSONObject()
        .apply {
            put("vendors", JSONArray(vendors))
            put("categories", JSONArray(categories))
            put("legIntCategories", JSONArray(legIntCategories))
        }
        .toString()
}

/**
 * RESPONSE
 */

internal data class CustomConsentResp(val content: JSONObject)

internal fun CustomConsentResp.toSpCustomConsent(): SPCustomConsents = SPCustomConsents(content)

internal data class ConsentResp(
    val content: JSONObject,
    val userConsent: String?,
    val uuid: String?,
    val localState: String,
    var campaignType: CampaignType? = null
)
