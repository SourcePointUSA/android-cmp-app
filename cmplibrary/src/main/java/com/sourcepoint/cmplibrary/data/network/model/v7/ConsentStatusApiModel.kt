package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
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
    val customVendorsResponse: CustomVendorsResponse,
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
    val dateCreated: String
)

internal data class Vendor(
    val id: String,
    val name: String,
    val type: String
)

internal data class GranularStatus(
    val thisContent: JSONObject,
    val vendorConsent: String,
    val vendorLegInt: String,
    val purposeConsent: String,
    val purposeLegInt: String,
    val previousOptInAll: Boolean,
    val defaultConsent: Boolean
)

internal data class ConsentStatusCS(
    val thisContent: JSONObject,
    val rejectedAny: Boolean,
    val rejectedLI: Boolean,
    val consentedAll: Boolean,
    val hasConsentData: Boolean,
    val consentedToAny: Boolean,
    val granularStatus: GranularStatus?
)
