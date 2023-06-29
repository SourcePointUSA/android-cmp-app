package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject
import java.util.* //ktlint-disable

internal fun String.toConsentActionOptimized(): Either<ConsentActionImplOptimized> = check {
    JsonConverter.converter.decodeFromString<ConsentActionImplOptimized>(this)
}

internal fun String.toConsentAction(): ConsentActionImpl {

    val map: Map<String, Any?> = JSONObject(this).toTreeMap()

    val actionType = (map["actionType"] as? Int)?.let { ActionType.values().find { v -> v.code == it } }
    val choiceId = (map["choiceId"] as? String)
    val legislation = map.getFieldValue<String>("legislation") ?: "CCPA"
    val privacyManagerId = (map["pmId"] as? String) ?: (map["localPmId"] as? String)
    val pmTab = (map["pmTab"] as? String)
    val requestFromPm = map.getFieldValue<Boolean>("requestFromPm") ?: false
    val saveAndExitVariables = map.getMap("saveAndExitVariables")?.let { JSONObject(it) } ?: JSONObject()
    val saveAndExitVariables2 = map.getMap("saveAndExitVariables")
        ?.let { it.toJSONObj().toString() }
        ?.let { check { JsonConverter.converter.parseToJsonElement(it) }.getOrNull() }
        ?.let { it.jsonObject }
        ?: JsonObject(mapOf())
    val consentLanguage = map.getFieldValue<String>("consentLanguage") ?: "EN"
    val customActionId = map.getFieldValue<String>("customActionId")

    return ConsentActionImpl(
        actionType = actionType ?: ActionType.UNKNOWN,
        choiceId = choiceId,
        privacyManagerId = privacyManagerId,
        pmTab = pmTab,
        requestFromPm = requestFromPm,
        saveAndExitVariables = saveAndExitVariables,
        saveAndExitVariablesOptimized = saveAndExitVariables2,
        consentLanguage = consentLanguage,
        campaignType = CampaignType.valueOf(legislation),
        customActionId = customActionId,
        thisContent = map.toJSONObj(),
    )
}

internal fun Map<String, Any?>.toCCPAUserConsent(uuid: String?, applies: Boolean): CCPAConsentInternal {

    val rejectedCategories = getFieldValue<Iterable<Any?>>("rejectedCategories")
        ?.filterIsInstance(String::class.java)
        ?: failParam("Ccpa  rejectedCategories")

    val rejectedVendors = getFieldValue<Iterable<Any?>>("rejectedVendors")
        ?.filterIsInstance(String::class.java)
        ?: failParam("Ccpa  rejectedVendors")

    val status: CcpaStatus = getFieldValue<String>("status")
        ?.let { s ->
            CcpaStatus.values().find {
                it.name == s
            }
        }
        ?: fail("CCPAStatus cannot be null!!!")

    val childPmId: String? = check { getFieldValue<String>("childPmId") }.getOrNull()

    return CCPAConsentInternal(
        uuid = uuid,
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        status = status,
        childPmId = childPmId,
        applies = applies,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGDPRUserConsent(uuid: String?, applies: Boolean): GDPRConsentInternal {

    val tcData: Map<String, Any?> = getMap("TCData") ?: emptyMap()
    val vendorsGrants = getMap("grants")
        ?.map {
            Pair(
                it.key,
                ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
            )
        }
        ?.toMap() ?: failParam("grants")
    val vendorsGranted: Map<String, GDPRPurposeGrants> = getMap("grants")
        ?.map {
            Pair(
                it.key,
                GDPRPurposeGrants(
                    granted = ((it.value as? Map<String, Any?>)?.get("vendorGrant") as? Boolean) ?: false,
                    purposeGrants = ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>)
                        ?: emptyMap()
                )
            )
        }
        ?.toMap() ?: failParam("grants")
    val euConsent = getFieldValue<String>("euconsent") ?: failParam("euconsent")
    val customVendorsResponse = getMap("customVendorsResponse")
    val consentedVendors: List<String> =
        (customVendorsResponse?.get("consentedVendors") as? Iterable<TreeMap<String, String>>)?.map {
            it["_id"] ?: ""
        } ?: emptyList()
    val consentedPurposes: List<String> = vendorsGrants.toAcceptedCategories().toList()

    val childPmId: String? = check { getFieldValue<String>("childPmId") }.getOrNull()

    return GDPRConsentInternal(
        uuid = uuid,
        tcData = tcData,
        grants = vendorsGranted,
        euconsent = euConsent,
        acceptedCategories = consentedPurposes,
        childPmId = childPmId,
        applies = applies,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Map<String, Boolean>>.toAcceptedCategories(): Iterable<String> {
    val partitions = this.flatMap { it.value.toList() }.partition { it.second }
    val trueCategories = partitions.first.map { it.first }.toSet()
    val falseCategories = partitions.second.map { it.first }.toSet()
    return trueCategories.minus(falseCategories)
}
