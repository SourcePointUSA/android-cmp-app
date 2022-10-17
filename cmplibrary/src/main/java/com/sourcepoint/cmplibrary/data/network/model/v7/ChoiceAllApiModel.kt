package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import org.json.JSONObject

internal data class ChoiceAllResp(
    val ccpa: CcpaCA?,
    val gdpr: GdprCA?
)
// Response
internal data class CcpaCA(
    val applies: Boolean?,
    val consentedAll: Boolean,
    val dateCreated: String,
    val gpcEnabled: Boolean?,
    val rejectedCategories: Array<String>?,
    val rejectedVendors: Array<String>?,
    val rejectedAll: Boolean,
    val newUser: Boolean?,
    val status: String,
    val uspstring: String,
    val uuid: String?,
)
internal data class GdprCA(
    val addtlConsent: String?,
    val applies: Boolean?,
    val childPmId: String,
    val consentStatus: JSONObject?,
    val dateCreated: String?,
    val euconsent: String?,
    val grants: JSONObject?,
    val hasLocalData: Boolean?,
    val TCData: JSONObject?,
    val postPayload: PostPayload?,
)

internal data class PostPayload(
    val consentAllRef: String,
    val granularStatus: JSONObject?,
    val vendorListId: String,
)

// Request
internal data class ChoiceAllParamReq(
    val env: Env,
//    val metadata: MetadataReq,
    val metadata: String,

    val propertyId: Int,
    val hasCsp: Boolean,
    val accountId: Int,
    val includeCustomVendorsRes: Boolean,
)
internal data class MetadataReq(
    val ccpa: CampaignReq,
    val gdpr: CampaignReq,
)
internal data class CampaignReq(
    val applies: Boolean?,
)
