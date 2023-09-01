package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.consentStatus.ConsentStatusMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeData
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.generateCcpaUspString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ConsentStatusParamReq(
    @SerialName("env")
    val env: Env,
    @SerialName("metadata")
    val metadata: ConsentStatusMetaData,
    @SerialName("propertyId")
    val propertyId: Int,
    @SerialName("accountId")
    val accountId: Int,
    @SerialName("authId")
    val authId: String?,
    @SerialName("localState")
    val localState: JsonElement?,
    @SerialName("hasCsp")
    val hasCsp: Boolean = false,
    @SerialName("withSiteActions")
    val withSiteActions: Boolean = false,
    @SerialName("includeData")
    val includeData: IncludeData,
)

enum class GranularState {
    ALL,
    SOME,
    NONE
}

@Serializable
data class ConsentStatusResp(
    @SerialName("consentStatusData") val consentStatusData: ConsentStatusData?,
    @SerialName("localState") val localState: JsonElement?
) {
    @Serializable
    data class ConsentStatusData(
        @SerialName("ccpa") val ccpa: CcpaCS?,
        @SerialName("gdpr") val gdpr: GdprCS?
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}

@Serializable
data class CcpaCS(
    @SerialName("applies")
    val applies: Boolean?,
    @SerialName("ccpaApplies")
    val ccpaApplies: Boolean?,
    @SerialName("consentedAll")
    val consentedAll: Boolean?,
    @SerialName("dateCreated")
    val dateCreated: String?,
    @SerialName("gpcEnabled")
    val gpcEnabled: Boolean?,
    @SerialName("newUser")
    val newUser: Boolean?,
    @SerialName("rejectedAll")
    val rejectedAll: Boolean?,
    @SerialName("rejectedCategories")
    val rejectedCategories: List<String>?,
    @SerialName("rejectedVendors")
    val rejectedVendors: List<String>?,
    @SerialName("signedLspa")
    val signedLspa: Boolean?,
    @Serializable(with = CcpaStatusSerializer::class)
    val status: CcpaStatus,
    @SerialName("GPPData")
    @Serializable(with = JsonMapSerializer::class)
    val gppData: Map<String, JsonElement>? = null,
    @SerialName("uuid")
    var uuid: String?,
    @SerialName("webConsentPayload")
    val webConsentPayload: JsonObject? = null,
) {

    val uspstring: String
        get() = generateCcpaUspString(
            applies = applies ?: false,
            ccpaStatus = status,
            signedLspa = signedLspa,
        )

    fun toPreloadConsent(): String {
        return JsonConverter.converter.encodeToString(
            CCPAConsentPreload(
                rejectedCategories = rejectedCategories ?: emptyList(),
                rejectedVendors = rejectedVendors ?: emptyList(),
                rejectedAll = rejectedAll ?: false
            )
        )
    }
}

@Serializable
data class GdprCS(
    @SerialName("applies")
    val applies: Boolean?,
    @SerialName("gdprApplies")
    val gdprApplies: Boolean?,
    @SerialName("categories")
    val categories: List<String>?,
    @SerialName("consentAllRef")
    val consentAllRef: String?,
    @SerialName("consentedToAll")
    val consentedToAll: Boolean?,
    @SerialName("legIntCategories")
    val legIntCategories: List<String>?,
    @SerialName("legIntVendors")
    val legIntVendors: List<String>?,
    @SerialName("postPayload")
    val postPayload: PostPayload?,
    @SerialName("rejectedAny")
    val rejectedAny: Boolean?,
    @SerialName("specialFeatures")
    val specialFeatures: List<String>?,
    @SerialName("vendors")
    val vendors: List<String>?,
    @SerialName("addtlConsent")
    val addtlConsent: String?,
    @SerialName("consentStatus")
    val consentStatus: ConsentStatus,
    @SerialName("cookieExpirationDays")
    val cookieExpirationDays: Int?,
    @SerialName("customVendorsResponse")
    val customVendorsResponse: CustomVendorsResponse?,
    @SerialName("dateCreated")
    val dateCreated: String?,
    @SerialName("euconsent")
    val euconsent: String?,
    @Serializable(with = GrantsSerializer::class)
    val grants: Map<String, GDPRPurposeGrants>?,
    @Serializable(with = JsonMapSerializer::class)
    val TCData: Map<String, JsonElement>?,
    @SerialName("localDataCurrent")
    val localDataCurrent: Boolean?,
    @SerialName("uuid")
    var uuid: String?,
    @SerialName("vendorListId")
    val vendorListId: String?,
    @SerialName("webConsentPayload")
    val webConsentPayload: JsonObject? = null,
) {

    @Serializable
    data class PostPayload(
        @SerialName("consentAllRef") val consentAllRef: String?,
        @SerialName("granularStatus") val granularStatus: ConsentStatus.GranularStatus,
        @SerialName("vendorListId") val vendorListId: String?
    )

    @Serializable
    data class CustomVendorsResponse(
        @SerialName("consentedPurposes") val consentedPurposes: List<ConsentedPurpose>?,
        @SerialName("consentedVendors") val consentedVendors: List<ConsentedVendor>?,
        @SerialName("legIntPurposes") val legIntPurposes: List<LegIntPurpose>?
    ) {
        @Serializable
        data class ConsentedPurpose(
            @SerialName("_id") val id: String?,
            @SerialName("name") val name: String?
        )

        @Serializable
        data class ConsentedVendor(
            @SerialName("_id") val id: String?,
            @SerialName("name") val name: String?,
            @SerialName("vendorType") val vendorType: String?
        )

        @Serializable
        data class LegIntPurpose(
            @SerialName("_id") val id: String?,
            @SerialName("name") val name: String?
        )
    }

    fun toPreloadConsent(): String {
        return JsonConverter.converter.encodeToString(
            GDPRConsentPreload(
                categories = categories ?: emptyList(),
                legIntCategories = legIntCategories ?: emptyList(),
                vendors = vendors ?: emptyList(),
                legIntVendors = legIntVendors ?: emptyList(),
                specialFeatures = specialFeatures ?: emptyList(),
                hasConsentData = consentStatus.hasConsentData ?: false
            )
        )
    }
}

@Serializable
data class GDPRConsentPreload(
    val categories: List<String>,
    val legIntCategories: List<String>,
    val vendors: List<String>,
    val legIntVendors: List<String>,
    val specialFeatures: List<String>,
    val hasConsentData: Boolean
)

@Serializable
data class CCPAConsentPreload(
    val rejectedAll: Boolean,
    val rejectedCategories: List<String>,
    val rejectedVendors: List<String>
)
