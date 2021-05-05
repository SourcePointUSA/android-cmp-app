package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject

internal fun String.toConsentAction(): ConsentAction {

    val map: Map<String, Any?> = JSONObject(this).toTreeMap()

    val actionType = (map["actionType"] as? Int)?.let { ActionType.values().find { v -> v.code == it } }
    val choiceId = (map["choiceId"] as? String)
    val legislation = map.getFieldValue<String>("legislation") ?: "CCPA"
    val privacyManagerId = (map["pmId"] as? String) ?: (map["localPmId"] as? String)
    val pmTab = (map["pmTab"] as? String)
    val requestFromPm = map.getFieldValue<Boolean>("requestFromPm") ?: false
    val saveAndExitVariables = map.getMap("saveAndExitVariables")?.let { JSONObject(it) } ?: JSONObject()
    val consentLanguage = map.getFieldValue<String>("consentLanguage") ?: "EN"

    return ConsentAction(
        actionType = actionType ?: ActionType.ACCEPT_ALL,
        choiceId = choiceId,
        privacyManagerId = privacyManagerId,
        pmTab = pmTab,
        requestFromPm = requestFromPm,
        saveAndExitVariables = saveAndExitVariables,
        consentLanguage = consentLanguage,
        campaignType = CampaignType.valueOf(legislation)
    )
}

internal fun Map<String, Any?>.toCCPAUserConsent(): CCPAConsent {

    val rejectedCategories = getFieldValue<Iterable<Any?>>("rejectedCategories")
        ?.filterIsInstance(String::class.java)
        ?: failParam("Ccpa  rejectedCategories")

    val rejectedVendors = getFieldValue<Iterable<Any?>>("rejectedVendors")
        ?.filterIsInstance(String::class.java)
        ?: failParam("Ccpa  rejectedVendors")

    val status: String = getFieldValue<String>("status")
        ?: fail("CCPAStatus cannot be null!!!")

    val uspString: String = getFieldValue("USPString") ?: "" // failParam("Ccpa USPString")

    return CCPAConsent(
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        status = status,
        uspstring = uspString,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGDPRUserConsent(): GDPRConsent {

    val userConsentMap = this
    /*
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
    */
    val tcData: Map<String, Any?> = (userConsentMap["TCData"] as? Map<String, Any?>) ?: emptyMap<String, Any?>()
    val vg = (userConsentMap["grants"] as? Map<String, Any?>) ?: emptyMap<String, Any?>()
    val vendorsGrants = vg.map {
        Pair(
            it.key,
            ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
        )
    }.toMap()
    val euconsent = (userConsentMap["euconsent"] as? String) ?: ""

    return GDPRConsent(
        thisContent = JSONObject(this),
//        acceptedCategories = acceptedCategories,
//        acceptedVendors = acceptedVendors,
//        legIntCategories = legIntCategories,
//        specialFeatures = specialFeatures,
        tcData = tcData,
        vendorsGrants = vendorsGrants,
        euconsent = euconsent
    )
}
