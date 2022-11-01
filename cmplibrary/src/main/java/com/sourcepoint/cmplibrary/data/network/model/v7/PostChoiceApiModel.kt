package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.GrantsSerializer
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

internal class PostChoiceParamReq(
    val env: Env,
    val actionType: ActionType,
    val body: JsonObject = JsonObject(mapOf())
)

@Serializable
data class GdprPostChoiceResp(
    @SerialName("acceptedCategories") val acceptedCategories: List<String>?,
    @SerialName("acceptedVendors") val acceptedVendors: List<String>?,
    @SerialName("addtlConsent") val addtlConsent: String?,
    @SerialName("consentStatus") val consentStatus: ConsentStatus?,
    @SerialName("consentedToAll") val consentedToAll: Boolean?,
    @SerialName("cookies") val cookies: List<Cooky?>?,
    @SerialName("dateCreated") val dateCreated: String?,
    @SerialName("euconsent") val euconsent: String?,
    @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
    @SerialName("legIntCategories") val legIntCategories: List<String>?,
    @SerialName("rejectedAny") val rejectedAny: Boolean?,
    @SerialName("specialFeatures") val specialFeatures: List<String>?,
    @SerialName("uuid") val uuid: String?
) {
    @Serializable
    data class ConsentStatus(
        @SerialName("consentedAll") val consentedAll: Boolean?,
        @SerialName("consentedToAny") val consentedToAny: Boolean?,
        @SerialName("granularStatus") val granularStatus: GranularStatus?,
        @SerialName("hasConsentData") val hasConsentData: Boolean?,
        @SerialName("rejectedAny") val rejectedAny: Boolean?,
        @SerialName("rejectedLI") val rejectedLI: Boolean?
    ) {
        @Serializable
        data class GranularStatus(
            @SerialName("defaultConsent") val defaultConsent: Boolean?,
            @SerialName("previousOptInAll") val previousOptInAll: Boolean?,
            @SerialName("purposeConsent") val purposeConsent: String?,
            @SerialName("purposeLegInt") val purposeLegInt: String?,
            @SerialName("vendorConsent") val vendorConsent: String?,
            @SerialName("vendorLegInt") val vendorLegInt: String?
        )
    }
}

@Serializable
data class CcpaPostChoiceResp(
    @SerialName("actions") val actions: List<Action?>?,
    @SerialName("applies") val applies: Boolean?,
    @SerialName("consentedAll") val consentedAll: Boolean?,
    @SerialName("cookies") val cookies: List<Cooky?>?,
    @SerialName("dateCreated") val dateCreated: String?,
    @SerialName("gpcEnabled") val gpcEnabled: Boolean?,
    @SerialName("rejectedAll") val rejectedAll: Boolean?,
    @SerialName("rejectedCategories") val rejectedCategories: List<String>?,
    @SerialName("rejectedVendors") val rejectedVendors: List<String>?,
    @SerialName("signedLspa") val signedLspa: Boolean?,
    @SerialName("status") val status: String?,
    @SerialName("uspstring") val uspstring: String?,
    @SerialName("uuid") val uuid: String?
) {
    @Serializable
    data class Action(
        @SerialName("_id") val id: String?,
        @SerialName("js") val js: String?,
        @SerialName("onStatusChangeOnly") val onStatusChangeOnly: Boolean?,
        @SerialName("tagManager") val tagManager: String,
        @SerialName("type") val type: String?,
        @SerialName("url") val url: String?
    )

    @Serializable
    data class Cooky(
        @SerialName("key") val key: String?,
        @SerialName("maxAge") val maxAge: Int?,
        @SerialName("setPath") val setPath: Boolean?,
        @SerialName("value") val value: String
    )
}
