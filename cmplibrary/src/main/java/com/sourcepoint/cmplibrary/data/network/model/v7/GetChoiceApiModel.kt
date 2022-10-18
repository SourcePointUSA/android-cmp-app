package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.GrantsSerializer
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

internal class ChoiceParamReq(
    val env: Env,
    val choiceType: ChoiceTypeParam,
    val metadata: String,
    val propertyId: Long,
    val accountId: Long,
    val body: JsonObject = JsonObject(mapOf())
)

enum class ChoiceTypeParam(val type: String) {
    CONSENT_ALL("consent-all"),
    REJECT_ALL("reject-all")
}

@Serializable
data class ChoiceResp(
    @SerialName("ccpa") val ccpa: Ccpa?,
    @SerialName("gdpr") val gdpr: Gdpr?
) {
    @Serializable
    data class Ccpa(
        @SerialName("actions") val actions: List<Action>?,
        @SerialName("applies") val applies: Boolean?,
        @SerialName("consentedAll") val consentedAll: Boolean?,
        @SerialName("cookies") val cookies: List<Cooky>?,
        @SerialName("dateCreated") val dateCreated: String?,
        @SerialName("gpcEnabled") val gpcEnabled: Boolean?,
        @SerialName("rejectedAll") val rejectedAll: Boolean?,
        @SerialName("rejectedCategories") val rejectedCategories: List<String>?,
        @SerialName("rejectedVendors") val rejectedVendors: List<String>?,
        @SerialName("signedLspa") val signedLspa: Boolean?,
        @SerialName("status") val status: String?
    ) {
        @Serializable
        data class Action(
            @SerialName("_id") val id: String?,
            @SerialName("js") val js: String?,
            @SerialName("onStatusChangeOnly") val onStatusChangeOnly: Boolean?,
            @SerialName("tagManager") val tagManager: JsonElement?,
            @SerialName("type") val type: String?,
            @SerialName("url") val url: String?
        )

        @Serializable
        data class Cooky(
            @SerialName("key") val key: String?,
            @SerialName("maxAge") val maxAge: Int?,
            @SerialName("setPath") val setPath: Boolean?,
            @SerialName("value") val value: String?
        )
    }

    @Serializable
    data class Gdpr(
        @SerialName("addtlConsent") val addtlConsent: String?,
        @SerialName("applies") val applies: Boolean?,
        @SerialName("categories") val categories: List<String>?,
        @SerialName("consentAllRef") val consentAllRef: String?,
        @SerialName("consentStatus") val consentStatus: ConsentStatus?,
        @SerialName("consentedToAll") val consentedToAll: Boolean?,
        @SerialName("cookies") val cookies: List<Cooky>?,
        @SerialName("customVendorsResponse") val customVendorsResponse: ConsentStatusResp.ConsentStatusData.GdprCS.CustomVendorsResponse?,
        @SerialName("euconsent") val euconsent: String?,
        @SerialName("gdprApplies") val gdprApplies: Boolean?,
        @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
        @SerialName("legIntCategories") val legIntCategories: List<String>?,
        @SerialName("legIntVendors") val legIntVendors: List<String>?,
        @SerialName("postPayload") val postPayload: PostPayload?,
        @SerialName("rejectedAny") val rejectedAny: Boolean?,
        @SerialName("specialFeatures") val specialFeatures: List<String>?,
        @SerialName("vendorListId") val vendorListId: String?,
        @SerialName("vendors") val vendors: List<String>?
    ) {
        @Serializable
        data class ConsentStatus(
            @SerialName("consentedAll") val consentedAll: Boolean?,
            @SerialName("consentedToAny") val consentedToAny: Boolean?,
            @SerialName("granularStatus") val granularStatus: com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatus.GranularStatus?,
            @SerialName("hasConsentData") val hasConsentData: Boolean?,
            @SerialName("rejectedAny") val rejectedAny: Boolean?,
            @SerialName("rejectedLI") val rejectedLI: Boolean?
        )

        @Serializable
        data class Cooky(
            @SerialName("key") val key: String?,
            @SerialName("maxAge") val maxAge: Int?,
            @SerialName("shareRootDomain") val shareRootDomain: Boolean?,
            @SerialName("value") val value: String?
        )

        @Serializable
        data class PostPayload(
            @SerialName("consentAllRef") val consentAllRef: String?,
            @SerialName("granularStatus") val granularStatus: com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatus.GranularStatus,
            @SerialName("vendorListId") val vendorListId: String?
        )
    }
}
