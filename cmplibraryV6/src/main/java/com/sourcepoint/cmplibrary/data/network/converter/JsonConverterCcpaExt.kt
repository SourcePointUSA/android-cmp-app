package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.CCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject

internal fun String.toCCPA(): Ccpa? {
    val map: Map<String, Any?> = JSONObject(this).toTreeMap()
    return map.toCCPA()
}

internal fun Map<String, Any?>.toCCPA(): Ccpa? {

    val map: Map<String, Any?> = this

    return map.let {

        val uuid = it.getFieldValue<String>("uuid") ?: failParam("uuid")
        val meta = it.getFieldValue<String>("meta") ?: failParam("meta")
        val ccpaApplies = it.getFieldValue<Boolean>("ccpaApplies") ?: failParam("meta")
        val message = it.getMap("message")
        val userConsentMap = it.getMap("userConsent") ?: failParam("userConsent")

        val messageObj = message?.let { map -> JSONObject(map) }

        Ccpa(
            uuid = uuid,
            userConsent = userConsentMap.toCCPAUserConsent(),
            meta = meta,
            ccpaApplies = ccpaApplies,
            message = messageObj,
            thisContent = JSONObject(map)
        )
    }
}

internal fun Map<String, Any?>.toCCPAUserConsent(): CCPAConsent {

    val rejectedCategories = getFieldValue<Iterable<Any?>>("rejectedCategories")
        ?.filterNotNull()
        ?: failParam("Ccpa  rejectedCategories")

    val rejectedVendors = getFieldValue<Iterable<Any?>>("rejectedVendors")
        ?.filterNotNull()
        ?: failParam("Ccpa  rejectedVendors")

    val status: String = getFieldValue<String>("status")
        ?: fail("CCPAStatus cannot be null!!!")

    val uspString : String = getFieldValue("USPString") ?: failParam("Ccpa USPString")
    val rejectedAll : Boolean = getFieldValue("rejectedAll") ?: true

    return CCPAConsent(
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        rejectedAll = rejectedAll,
        status = status,
        uspstring = uspString,
        thisContent = JSONObject(this)
    )
}
