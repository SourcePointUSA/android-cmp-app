package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.MessageCategory
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toJSONObj
import org.json.JSONObject
import java.util.* // ktlint-disable

internal fun Map<String, Any?>.toConsentStatusResp(): ConsentStatusResp {

    val localState = getMap("localState")?.toJSONObj()
    val consentStatusData =
        getMap("consentStatusData")?.toConsentStatusData() ?: throw RuntimeException("ConsentStatusData is missing!!!")

    return ConsentStatusResp(
        localState = localState ?: JSONObject(),
        consentStatusData = consentStatusData,
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toConsentStatusData(): ConsentStatusData {
    val gdprCS = getMap("gdpr")?.toGdprCS()
    val ccpaCS = getMap("ccpa")?.toCcpaCS()
    return ConsentStatusData(JSONObject(this), gdprCS, ccpaCS)
}

internal fun Map<String, Any?>.toMessageMetaData(): MessageMetaData {
    val messageSubCategory = MessageSubCategory.values()
        .find { m -> m.code == getFieldValue<Int>("subCategoryId") }
        ?: MessageSubCategory.TCFv2
    val messageCategory = MessageCategory.values()
        .find { m -> m.code == getFieldValue<Int>("categoryId") }
        ?: failParam("MessageCategory")

    return MessageMetaData(
        bucket = getFieldValue("bucket"),
        categoryId = messageCategory,
        messageId = getFieldValue<Int>("messageId"),
        msgDescription = getFieldValue<String>("msgDescription"),
        prtnUUID = getFieldValue<String>("prtnUUID"),
        subCategoryId = messageSubCategory
    )
}

internal fun Map<String, Any?>.toConsentStatus(): ConsentStatusCS {
    return ConsentStatusCS(
        granularStatus = getMap("granularStatus")?.toGranularStatus(),
        consentedAll = getFieldValue<Boolean>("consentedAll") ?: failParam("consentedAll"),
        hasConsentData = getFieldValue<Boolean>("hasConsentData") ?: failParam("hasConsentData"),
        consentedToAny = getFieldValue<Boolean>("consentedToAny"),
        rejectedAny = getFieldValue<Boolean>("rejectedAny") ?: failParam("rejectedAny"),
        rejectedLI = getFieldValue<Boolean>("rejectedLI") ?: failParam("rejectedLI"),
        thisContent = JSONObject(this)
    )
}

internal fun Map<String, Any?>.toGranularStatus(): GranularStatus {

    val purposeConsent: GranularState = getFieldValue<String>("purposeConsent")
        ?.let { s ->
            GranularState.values().find {
                it.name == s
            }
        }
        ?: fail("GranularState purposeConsent cannot be null!!!")

    val purposeLegInt: GranularState = getFieldValue<String>("purposeLegInt")
        ?.let { s ->
            GranularState.values().find {
                it.name == s
            }
        }
        ?: fail("GranularState purposeLegInt cannot be null!!!")

    val vendorConsent: GranularState = getFieldValue<String>("vendorConsent")
        ?.let { s ->
            GranularState.values().find {
                it.name == s
            }
        }
        ?: fail("GranularState vendorConsent cannot be null!!!")

    val vendorLegInt: GranularState = getFieldValue<String>("vendorLegInt")
        ?.let { s ->
            GranularState.values().find {
                it.name == s
            }
        }
        ?: fail("GranularState vendorLegInt cannot be null!!!")

    return GranularStatus(
        defaultConsent = getFieldValue<Boolean>("defaultConsent") ?: failParam("defaultConsent"),
        previousOptInAll = getFieldValue<Boolean>("previousOptInAll") ?: failParam("previousOptInAll"),
        purposeConsent = purposeConsent,
        purposeLegInt = purposeLegInt,
        vendorConsent = vendorConsent,
        vendorLegInt = vendorLegInt,
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

internal fun Map<String, Any?>.toCcpaCS(): CcpaCS {

    val uuid = getFieldValue<String>("uuid")
    val dateCreated = getFieldValue<String>("dateCreated")
    val uspstring = getFieldValue<String>("uspstring") ?: failParam("uspstring")

    val applies = getFieldValue<Boolean>("ccpaApplies") ?: false
    val consentedAll = getFieldValue<Boolean>("consentedAll") ?: false
    val gpcEnabled = getFieldValue<Boolean>("gpcEnabled") ?: false
    val newUser = getFieldValue<Boolean>("newUser") ?: false
    val rejectedAll = getFieldValue<Boolean>("rejectedAll") ?: false
    val signedLspa = getFieldValue<Boolean>("signedLspa") ?: false

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

    return CcpaCS(
        thisContent = JSONObject(this),
        ccpaApplies = applies,
        consentedAll = consentedAll,
        uuid = uuid,
        dateCreated = dateCreated,
        gpcEnabled = gpcEnabled,
        newUser = newUser,
        rejectedAll = rejectedAll,
        rejectedCategories = rejectedCategories,
        rejectedVendors = rejectedVendors,
        signedLspa = signedLspa,
        uspstring = uspstring,
        status = status
    )
}

internal fun Map<String, Any?>.toGdprCS(): GdprCS {

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
        grants = vendorsGranted,
        euconsent = euConsent,
        addtlConsent = addtlConsent,
        dateCreated = dateCreated,
        vendorListId = vendorListId,
        uuid = uuid,
        gdprApplies = gdprApplies,
        localDataCurrent = localDataCurrent,
        cookieExpirationDays = cookieExpirationDays,
        consentStatus = consentStatus
    )
}
