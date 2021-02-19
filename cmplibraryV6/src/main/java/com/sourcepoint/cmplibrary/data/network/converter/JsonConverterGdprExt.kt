package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.GDPRConsent
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.data.network.model.MessageGdprResp
import org.json.JSONObject

internal fun DeferredMap.toGDPR(): Gdpr? {
    val map: MutableMap<String, Any> = this
    return map.toGDPR()
}

internal fun String.toGDPR(): Gdpr? {
    val map: MutableMap<String, Any> = JSON.std.mapFrom(this)
    return map.toGDPR()
}

internal fun MutableMap<String, Any>.toGDPR(): Gdpr? {

    val map: MutableMap<String, Any> = this

    return map.let {
        val uuid = (it["uuid"] as? String) ?: failParam("uuid")
        val meta = (it["meta"] as? String) ?: failParam("meta")
        val message = (it["message"] as? DeferredMap)
        val userConsentMap = (it["userConsent"] as? DeferredMap) ?: failParam("userConsent")

        val messageObj = message?.let { JSONObject(JSON.std.asString(it)) }

        Gdpr(
            uuid = uuid,
            userConsent = userConsentMap.toGDPRUserConsent(),
            meta = meta,
            message = messageObj
        )
    }
}

internal fun DeferredMap.toMessageGdprResp(): MessageGdprResp {
    return MessageGdprResp(
        message_json = (this["message_json"] as? DeferredMap).let { m -> JSON.std.asString(m) }
            ?: failParam("message_json"),
        categories = (this["categories"] as? Iterable<Any?>).let { m -> JSON.std.asString(m) }
            ?: failParam("categories"),
        language = (this["language"] as? String).let { m -> JSON.std.asString(m) } ?: failParam("language"),
        message_choice = (this["message_choice"] as? Iterable<Any?>).let { m -> JSON.std.asString(m) }
            ?: failParam("message_choice"),
        site_id = (this["site_id"] as? Int).let { m -> JSON.std.asString(m) } ?: failParam("site_id"),
    )
}

internal fun DeferredMap.toGDPRUserConsent(): GDPRConsent {

    val userConsentMap = (this as? DeferredMap) ?: failParam("GDPRUserConsent")

    val acceptedCategories = (userConsentMap["acceptedCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: failParam("acceptedCategories")

    val acceptedVendors = (userConsentMap["acceptedVendors"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: failParam("acceptedVendors")

    val legIntCategories = (userConsentMap["legIntCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: failParam("legIntCategories")

    val specialFeatures = (userConsentMap["specialFeatures"] as? Iterable<Any?>)?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: failParam("specialFeatures")

    val tcData = (userConsentMap["TCData"] as? DeferredMap) ?: DeferredMap(false)
    val vendorsGrants = (userConsentMap["grants"] as? DeferredMap) ?: DeferredMap(false)
    val euconsent = (userConsentMap["euconsent"] as? String) ?: ""

    return GDPRConsent(
        acceptedCategories = acceptedCategories,
        acceptedVendors = acceptedVendors,
        legIntCategories = legIntCategories,
        specialFeatures = specialFeatures,
        tcData = tcData,
        vendorsGrants = vendorsGrants,
        euconsent = euconsent
    )
}
