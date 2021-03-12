package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.GDPRConsent
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject

internal fun String.toGDPR1203(): Gdpr? {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toGDPR()
}

internal fun Map<String, Any?>.toGDPR1203(): Gdpr {

    val map: Map<String, Any?> = this

    return map.let {
        val uuid = it.getFieldValue<String>("uuid") ?: failParam("uuid")
        val meta = it.getFieldValue<String>("meta") ?: failParam("meta")
        val message = it.getMap("message")
        val userConsentMap = it.getMap("userConsent") ?: failParam("userConsent")

        val messageObj = message?.let { map -> JSONObject(map) }

        Gdpr(
            uuid = uuid,
            userConsent = userConsentMap.toGDPRUserConsent1203(),
            meta = meta,
            message = messageObj,
            thisContent = JSONObject(map)
        )
    }
}

internal fun Map<String, Any?>.toGDPRUserConsent1203(): GDPRConsent {

    val userConsentMap = this

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

    val tcData = (userConsentMap["TCData"] as? Map<String, Any?>) ?: emptyMap<String, Any?>()
    val vendorsGrants = (userConsentMap["grants"] as? Map<String, Any?>) ?: emptyMap<String, Any?>()
    val euconsent = (userConsentMap["euconsent"] as? String) ?: ""

    return GDPRConsent(
        thisContent = JSONObject(this),
        acceptedCategories = acceptedCategories,
        acceptedVendors = acceptedVendors,
        legIntCategories = legIntCategories,
        specialFeatures = specialFeatures,
        tcData = tcData,
        vendorsGrants = vendorsGrants,
        euconsent = euconsent
    )
}
