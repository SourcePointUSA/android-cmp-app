package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.GDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.data.network.model.MessageGdprResp
import com.sourcepoint.gdpr_cmplibrary.exception.InvalidResponseWebMessageException
import org.json.JSONObject

internal fun String.toGDPR(): Gdpr? {

    val map: MutableMap<String, Any> = JSON.std.mapFrom(this)

    return (map["gdpr"] as? DeferredMap)?.let {
        val uuid = (it["uuid"] as? String) ?: fail("uuid")
        val meta = (it["meta"] as? String) ?: fail("meta")
        val message = (it["message"] as? DeferredMap) ?: fail("message")
        val userConsentMap = (it["userConsent"] as? DeferredMap) ?: fail("userConsent")

        val messageObj = JSONObject(JSON.std.asString(message))

        Gdpr(
            uuid = uuid,
            GDPRUserConsent = userConsentMap.toGDPRUserConsent(),
            meta = meta,
            message = messageObj
        )
    }
}

internal fun DeferredMap.toMessageGdprResp(): MessageGdprResp {
    return MessageGdprResp(
        message_json = (this["message_json"] as? DeferredMap).let { m -> JSON.std.asString(m) } ?: fail("message_json"),
        categories = (this["categories"] as? Iterable<Any?>).let { m -> JSON.std.asString(m) } ?: fail("categories"),
        language = (this["language"] as? String).let { m -> JSON.std.asString(m) } ?: fail("language"),
        message_choice = (this["message_choice"] as? Iterable<Any?>).let { m -> JSON.std.asString(m) } ?: fail("message_choice"),
        site_id = (this["site_id"] as? Int).let { m -> JSON.std.asString(m) } ?: fail("site_id"),
    )
}

internal fun DeferredMap.toGDPRUserConsent(): GDPRUserConsent {

    val userConsentMap = (this as? DeferredMap) ?: fail("userConsent")

    val acceptedCategories = (userConsentMap["acceptedCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: fail("acceptedCategories")

    val acceptedVendors = (userConsentMap["acceptedVendors"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: fail("acceptedVendors")

    val legIntCategories = (userConsentMap["legIntCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: fail("legIntCategories")

    val specialFeatures = (userConsentMap["specialFeatures"] as? Iterable<Any?>)?.filterNotNull()
        ?: fail("specialFeatures")

    val tcData = (userConsentMap["TCData"] as? DeferredMap) ?: DeferredMap(false)
    val vendorsGrants = (userConsentMap["grants"] as? DeferredMap) ?: DeferredMap(false)
    val euconsent = (userConsentMap["euconsent"] as? String) ?: ""

    return GDPRUserConsent(
        acceptedCategories = acceptedCategories,
        acceptedVendors = acceptedVendors,
        legIntCategories = legIntCategories,
        specialFeatures = specialFeatures,
        tcData = tcData,
        vendorsGrants = vendorsGrants,
        euconsent = euconsent
    )
}

/**
 * Util method to throws a [ConsentLibExceptionK] with a custom message
 * @param param name of the null object
 */
private fun fail(param: String): Nothing {
    throw InvalidResponseWebMessageException(description = "$param object is null")
}
