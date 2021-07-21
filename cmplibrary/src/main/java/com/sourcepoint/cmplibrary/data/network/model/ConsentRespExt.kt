package com.sourcepoint.cmplibrary.data.network.model

import android.os.Build
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import java.util.* //ktlint-disable

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
        campaignType = CampaignType.valueOf(legislation),
        thisContent = map.toJSONObj()
    )
}

internal fun Map<String, Any?>.toCCPAUserConsent(uuid: String?): CCPAConsentInternal {

    val rejectedCategories = getFieldValue<Iterable<Any?>>("rejectedCategories")
        ?.filterIsInstance(String::class.java)
        ?: failParam("Ccpa  rejectedCategories")

    val rejectedVendors = getFieldValue<Iterable<Any?>>("rejectedVendors")
        ?.filterIsInstance(String::class.java)
        ?: failParam("Ccpa  rejectedVendors")

    val status: String = getFieldValue<String>("status")
        ?: fail("CCPAStatus cannot be null!!!")

    val uspString: String = getFieldValue("USPString") ?: "" // failParam("Ccpa USPString")

    return CCPAConsentInternal(
        uuid = uuid,
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        status = status,
        uspstring = uspString,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGDPRUserConsent(uuid: String?): GDPRConsentInternal {

    val tcData: Map<String, Any?> = getMap("TCData") ?: emptyMap()
    val vendorsGrants = getMap("grants")
        ?.map {
            Pair(
                it.key,
                ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
            )
        }
        ?.toMap() ?: failParam("grants")
    val euConsent = getFieldValue<String>("euconsent") ?: failParam("euconsent")
    val customVendorsResponse = getMap("customVendorsResponse")
    val consentedVendors: List<String> =
        (customVendorsResponse?.get("consentedVendors") as? Iterable<TreeMap<String, String>>)?.map {
            it.getOrDefaultCheckVersion(
                "_id",
                ""
            )
        } ?: emptyList()
    val consentedPurposes: List<String> =
        (customVendorsResponse?.get("consentedPurposes") as? Iterable<TreeMap<String, String>>)?.map {
            it.getOrDefaultCheckVersion(
                "_id",
                ""
            )
        } ?: emptyList()

    return GDPRConsentInternal(
        uuid = uuid,
        tcData = tcData,
        grants = vendorsGrants,
        euconsent = euConsent,
//        acceptedCategories = consentedPurposes,
//        acceptedVendors = consentedVendors,
        thisContent = JSONObject(this)
    )
}

internal fun TreeMap<String, String>.getOrDefaultCheckVersion(key : String, default : String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        getOrDefault(key, default)
    } else {
        get(key) ?: default
    }
}
