package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

internal fun String.toConsentAction(): ConsentAction {

    val map: Map<String, Any?> = JSONObject(this).toTreeMap()

    val actionType = (map["actionType"] as? Int)?.let { ActionType.values().find { v -> v.code == it } }
        ?: fail("actionType")
    val choiceId = (map["choiceId"] as? String)
    val legislation = map.getFieldValue<String>("legislation") ?: "CCPA"
    val privacyManagerId = (map["pmId"] as? String) ?: (map["localPmId"] as? String)
    val pmTab = (map["pmTab"] as? String)
    val requestFromPm = map.getFieldValue<Boolean>("requestFromPm") ?: false
    val saveAndExitVariables = map.getMap("saveAndExitVariables")?.let { JSONObject(it) } ?: JSONObject()
    val consentLanguage = map.getFieldValue<String>("consentLanguage") ?: "EN"

    return ConsentAction(
        actionType = actionType,
        choiceId = choiceId,
        privacyManagerId = privacyManagerId,
        pmTab = pmTab,
        requestFromPm = requestFromPm,
        saveAndExitVariables = saveAndExitVariables,
        consentLanguage = consentLanguage,
        legislation = Legislation.valueOf(legislation)
    )
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

    val uspString: String = getFieldValue("USPString") ?: "" // failParam("Ccpa USPString")
    val rejectedAll: Boolean = getFieldValue("rejectedAll") ?: true

    return CCPAConsent(
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        rejectedAll = rejectedAll,
        status = status,
        uspstring = uspString,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGDPRUserConsent(): GDPRConsent {

    val userConsentMap = this

    val acceptedCategories = (userConsentMap["acceptedCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: emptyList()

    val acceptedVendors = (userConsentMap["acceptedVendors"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: emptyList()

    val legIntCategories = (userConsentMap["legIntCategories"] as? Iterable<Any?>)
        ?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: emptyList()

    val specialFeatures = (userConsentMap["specialFeatures"] as? Iterable<Any?>)?.filterNotNull()
        ?.sortedBy { it.hashCode() }
        ?: emptyList()

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
