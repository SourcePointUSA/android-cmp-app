package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import org.json.JSONObject

internal class ConsentStatusResp(
    val thisContent: JSONObject,
    val consentStatusData: ConsentStatusData,
    val localState: JSONObject
)

internal data class ConsentStatusParamReq(
    val env: Env,
    val metadata: String,
    val propertyId: Int,
    val hasCsp: Boolean,
    val withSiteActions: Boolean,
    val accountId: Int,
    val authId: String? = null
)

internal data class ConsentStatusData(
    val thisContent: JSONObject,
    val gdprCS: GdprCS?,
    val ccpaCS: CcpaCS?
)

internal data class GdprCS(
    val thisContent: JSONObject,
    var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    val euconsent: String,
    val addtlConsent: String,
    val dateCreated: String,
    val consentUUID: String,
    val vendorListId: String,
    val uuid: String,
    val gdprApplies: Boolean,
    val localDataCurrent: Boolean,
    val cookieExpirationDays: Int,
    val consentStatus: ConsentStatusCS
)

internal data class CustomVendorsResponse(
    val consentedVendors: List<String>,
    val consentedPurposes: List<String>,
    val legIntPurposes: List<String>,
)

internal data class CcpaCS(
    val thisContent: JSONObject,
    val dateCreated: String?,
    val newUser: Boolean,
    val consentedAll: Boolean,
    val rejectedCategories: List<String> = listOf(),
    val rejectedVendors: List<String> = listOf(),
    val rejectedAll: Boolean,
    val status: CcpaStatus? = null,
    val signedLspa: Boolean,
    val uspstring: String,
    val gpcEnabled: Boolean,
    val uuid: String?,
    val ccpaApplies: Boolean
)

internal data class Vendor(
    val id: String,
    val name: String,
    val type: String
)

internal data class GranularStatus(
    val thisContent: JSONObject,
    val vendorConsent: GranularState,
    val vendorLegInt: GranularState,
    val purposeConsent: GranularState,
    val purposeLegInt: GranularState,
    val previousOptInAll: Boolean,
    val defaultConsent: Boolean
)

enum class GranularState {
    ALL,
    SOME,
    NONE
}

internal data class ConsentStatusCS(
    val thisContent: JSONObject,
    val rejectedAny: Boolean,
    val rejectedLI: Boolean,
    val consentedAll: Boolean,
    val hasConsentData: Boolean,
    val consentedToAny: Boolean,
    val granularStatus: GranularStatus?
)
