package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import org.json.JSONObject

internal class ChoiceAllResp(
    val thisContent: JSONObject,
    val ccpa: CcpaCA?,
    val gdpr: GdprCA?
)
// Response
internal class CcpaCA(
    val thisContent: JSONObject,
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
internal class GdprCA(
    val thisContent: JSONObject,
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

internal class PostPayload(
    val thisContent: JSONObject,
    val consentAllRef: String,
    val granularStatus: JSONObject?,
    val vendorListId: String,
)

// Request
internal class ChoiceAllParamReq(
    val env: Env,
//    val metadata: MetadataReq,
    val metadata: String,

    val propertyId: Int,
    val hasCsp: Boolean,
    val accountId: Int,
    val includeCustomVendorsRes: Boolean,
)
internal class MetadataReq(
    val ccpa: CampaignReq,
    val gdpr: CampaignReq,
)
internal class CampaignReq(
    val applies: Boolean?,
)
