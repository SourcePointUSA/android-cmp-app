package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.model.CCPAStatus
import com.sourcepoint.cmplibrary.data.network.model.CCPAUserConsent
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import org.json.JSONObject

internal fun DeferredMap.toCCPA(): Ccpa? {
    val map: MutableMap<String, Any> = this
    return map.toCCPA()
}

internal fun String.toCCPA(): Ccpa? {
    val map: MutableMap<String, Any> = JSON.std.mapFrom(this)
    return map.toCCPA()
}

internal fun MutableMap<String, Any>.toCCPA(): Ccpa? {

    val map: MutableMap<String, Any> = this

    return map.let {
        val uuid = (it["uuid"] as? String) ?: failParam("uuid")
        val meta = (it["meta"] as? String) ?: failParam("meta")
        val ccpaApplies = (it["ccpaApplies"] as? Boolean) ?: failParam("meta")
        val message = (it["message"] as? DeferredMap) ?: failParam("message")
        val userConsentMap = (it["userConsent"] as? DeferredMap) ?: failParam("userConsent")

        val messageObj = JSONObject(JSON.std.asString(message))

        Ccpa(
            uuid = uuid,
            userConsent = userConsentMap.toCCPAUserConsent(),
            meta = meta,
            ccpaApplies = ccpaApplies,
            message = messageObj
        )
    }
}

internal fun String.toCCPAStatus(): CCPAStatus {
    return CCPAStatus
        .values()
        .find { it.state == this }
        ?: fail("CCPAStatus[$this] not valid!!!")
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
