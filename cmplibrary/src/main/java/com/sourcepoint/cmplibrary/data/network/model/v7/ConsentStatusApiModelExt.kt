package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toJSONObj
import org.json.JSONObject
import java.util.* // ktlint-disable

internal fun Map<String, Any?>.toConsentStatusResp(): ConsentStatusResp {

    val localState = getMap("localState")?.toJSONObj()
    val consentStatusData = getMap("consentStatusData")?.toConsentStatusData() ?: throw RuntimeException("ConsentStatusData is missing!!!")

    return ConsentStatusResp(
        localState = localState ?: JSONObject(),
        consentStatusData = consentStatusData,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toConsentStatusData(): ConsentStatusData {
    val gdprCS = getMap("gdpr")?.toGdprCS()
    return ConsentStatusData(JSONObject(this), gdprCS, null)
}

internal fun Map<String, Any?>.toConsentStatus(): ConsentStatusCS {
    return ConsentStatusCS(
        granularStatus = getMap("granularStatus")?.toGranularStatus(),
        consentedAll = getFieldValue<Boolean>("consentedAll") ?: failParam("consentedAll"),
        hasConsentData = getFieldValue<Boolean>("hasConsentData") ?: failParam("hasConsentData"),
        consentedToAny = getFieldValue<Boolean>("consentedToAny") ?: failParam("consentedToAny"),
        rejectedAny = getFieldValue<Boolean>("rejectedAny") ?: failParam("rejectedAny"),
        rejectedLI = getFieldValue<Boolean>("rejectedLI") ?: failParam("rejectedLI"),
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGranularStatus(): GranularStatus {
    return GranularStatus(
        defaultConsent = getFieldValue<Boolean>("defaultConsent") ?: failParam("defaultConsent"),
        previousOptInAll = getFieldValue<Boolean>("previousOptInAll") ?: failParam("previousOptInAll"),
        purposeConsent = getFieldValue<String>("purposeConsent") ?: failParam("purposeConsent"),
        purposeLegInt = getFieldValue<String>("purposeLegInt") ?: failParam("purposeLegInt"),
        vendorConsent = getFieldValue<String>("vendorConsent") ?: failParam("vendorConsent"),
        vendorLegInt = getFieldValue<String>("vendorLegInt") ?: failParam("vendorLegInt"),
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toCustomVendorsResponse(): CustomVendorsResponse {
    val consentedVendors = (get("consentedVendors") as? Iterable<TreeMap<String, String>>)?.map {
        it["name"] ?: ""
    } ?: emptyList()

    val consentedPurposes = (get("consentedPurposes") as? Iterable<TreeMap<String, String>>)?.map {
        it["name"] ?: ""
    } ?: emptyList()

    val legIntPurposes = (get("legIntPurposes") as? Iterable<TreeMap<String, String>>)?.map {
        it["name"] ?: ""
    } ?: emptyList()

    return CustomVendorsResponse(
        consentedVendors = consentedVendors,
        consentedPurposes = consentedPurposes,
        legIntPurposes = legIntPurposes
    )
}

internal fun Map<String, Any?>.toGdprCS(): GdprCS {

    val customVendorsResponse = getMap("customVendorsResponse")?.toCustomVendorsResponse() ?: failParam("customVendorsResponse")

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
    val addtlConsent = getFieldValue<String>("addtlConsent") ?: failParam("addtlConsent")
    val dateCreated = getFieldValue<String>("dateCreated") ?: failParam("dateCreated")
    val consentUUID = getFieldValue<String>("consentUUID") ?: failParam("consentUUID")
    val vendorListId = getFieldValue<String>("vendorListId") ?: failParam("vendorListId")
    val uuid = getFieldValue<String>("uuid") ?: failParam("uuid")
    val gdprApplies = getFieldValue<Boolean>("gdprApplies") ?: failParam("gdprApplies")
    val localDataCurrent = getFieldValue<Boolean>("localDataCurrent") ?: failParam("localDataCurrent")
    val cookieExpirationDays = getFieldValue<Int>("cookieExpirationDays") ?: failParam("cookieExpirationDays")
    val consentStatus = getMap("consentStatus")?.toConsentStatus() ?: failParam("consentStatus")

    return GdprCS(
        thisContent = JSONObject(this),
        customVendorsResponse = customVendorsResponse,
        euconsent = euConsent,
        grants = vendorsGranted,
        addtlConsent = addtlConsent,
        dateCreated = dateCreated,
        consentUUID = consentUUID,
        vendorListId = vendorListId,
        uuid = uuid,
        gdprApplies = gdprApplies,
        localDataCurrent = localDataCurrent,
        cookieExpirationDays = cookieExpirationDays,
        consentStatus = consentStatus
    )
}
