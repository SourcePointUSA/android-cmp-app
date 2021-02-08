package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

internal fun String.toMessageRespDto(): MessageResp {
    val map: MutableMap<String, Any> = JSON.std.mapFrom(this)

    // check the number of message contained. We can only have 1 instance
    val numberOfMessages = map.toList().count { (it.second as DeferredMap).containsKey("message") }
    if (numberOfMessages != 1) fail("We have [$numberOfMessages] inst. of Message. Only one Message object can exist int the MessageResp!!!")

    // create a message JSONObject
    val legislationEntry = map.toList().first { (it.second as DeferredMap).containsKey("message") }

    val legislationContent = legislationEntry.second
    val uuid = (legislationContent as? DeferredMap)?.get("uuid") as? String ?: failParam("uuid")
    val meta = (legislationContent as? DeferredMap)?.get("meta") as? String ?: failParam("meta")
    val userConsentContent: Any = (legislationContent as? DeferredMap)?.get("userConsent") ?: failParam("userConsent")
    val message = (legislationContent as? DeferredMap)?.get("message") ?: failParam("message")
    val messageObj = JSONObject(JSON.std.asString(message))

    // extract the applied Legislation
    val legislation = legislationEntry.first.getAppliedLegislation()
    // extract userConsent
    val userConsent = (userConsentContent as? DeferredMap)?.toUserConsent(legislation) ?: fail("userConsent")

    // build the object
    return MessageResp(
        legislation = legislation,
        message = messageObj,
        meta = meta,
        uuid = uuid,
        userConsent = userConsent
    )
}

internal fun DeferredMap.toUserConsent(legislation: Legislation): UserConsent {
    return when (legislation) {
        Legislation.GDPR -> this.toGDPRUserConsent()
        Legislation.CCPA -> this.toCCPAUserConsent()
    }
}

internal fun String.toGDPR(): Gdpr? {

    val map: MutableMap<String, Any> = JSON.std.mapFrom(this)

    return (map["gdpr"] as? DeferredMap)?.let {
        val uuid = (it["uuid"] as? String) ?: failParam("uuid")
        val meta = (it["meta"] as? String) ?: failParam("meta")
        val message = (it["message"] as? DeferredMap) ?: failParam("message")
        val userConsentMap = (it["userConsent"] as? DeferredMap) ?: failParam("userConsent")

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

internal fun DeferredMap.toCCPAUserConsent(): CCPAUserConsent {
    val userConsentMap = (this as? DeferredMap) ?: failParam("CCPAUserConsent")

    val rejectedCategories = (userConsentMap["rejectedCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: failParam("rejectedCategories")

    val rejectedVendors = (userConsentMap["rejectedVendors"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: failParam("rejectedVendors")

    val status = (userConsentMap["status"] as? String)?.toCCPAStatus() ?: fail("CCPAStatus cannot be null!!!")

    val uspstring = (userConsentMap["uspstring"] as? String) ?: ""

    return CCPAUserConsent(
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        status = status,
        uspstring = uspstring
    )
}

internal fun DeferredMap.toGDPRUserConsent(): GDPRUserConsent {

    val userConsentMap = (this as? DeferredMap) ?: failParam("GDPRUserConsent")

    val acceptedCategories = (userConsentMap["acceptedCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: failParam("acceptedCategories")

    val acceptedVendors = (userConsentMap["acceptedVendors"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: failParam("acceptedVendors")

    val legIntCategories = (userConsentMap["legIntCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?: failParam("legIntCategories")

    val specialFeatures = (userConsentMap["specialFeatures"] as? Iterable<Any?>)?.filterNotNull()
        ?: failParam("specialFeatures")

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

internal fun String.toCCPAStatus(): CCPAStatus {
    return CCPAStatus
        .values()
        .find { it.state == this }
        ?: fail("CCPAStatus[$this] not valid!!!")
}

/**
 * Util method to throws a [ConsentLibExceptionK] with a custom message
 * @param param name of the null object
 */
private fun failParam(param: String): Nothing {
    throw InvalidResponseWebMessageException(description = "$param object is null")
}

private fun fail(message: String): Nothing {
    throw InvalidResponseWebMessageException(description = message)
}
