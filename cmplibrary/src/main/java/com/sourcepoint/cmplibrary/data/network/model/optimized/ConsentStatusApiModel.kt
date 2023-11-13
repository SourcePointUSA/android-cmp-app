package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ConsentStatusParamReq(
    @SerialName("env") val env: Env,
    @SerialName("metadata") val metadata: String,
    @SerialName("propertyId") val propertyId: Long,
    @SerialName("accountId") val accountId: Long,
    @SerialName("authId") val authId: String?,
    @SerialName("localState") val localState: JsonElement?
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
        @SerialName("gdpr") val gdpr: GdprCS?,
        @SerialName("usnat") val usnat: USNatConsentData?,
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}

@Serializable
data class CcpaCS(
    val applies: Boolean?,
    @SerialName("consentedAll") val consentedAll: Boolean?,
    @SerialName("dateCreated") val dateCreated: String?,
    @SerialName("gpcEnabled") val gpcEnabled: Boolean?,
    @SerialName("newUser") val newUser: Boolean?,
    @SerialName("rejectedAll") val rejectedAll: Boolean?,
    @SerialName("rejectedCategories") val rejectedCategories: List<String>?,
    @SerialName("rejectedVendors") val rejectedVendors: List<String>?,
    @SerialName("signedLspa") val signedLspa: Boolean?,
    @SerialName("uspstring") val uspstring: String? = null,
    @Serializable(with = CcpaStatusSerializer::class) val status: CcpaStatus?,
    @SerialName("GPPData") @Serializable(with = JsonMapSerializer::class) val gppData: Map<String, JsonElement>? = null,
    @SerialName("uuid") var uuid: String?,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject? = null,
    @SerialName("expirationDate") var expirationDate: String?,
)

@Serializable
data class USNatConsentData(
    val applies: Boolean?,
    @SerialName("consentStatus") val consentStatus: ConsentStatus?,
    @SerialName("consentString") val consentString: String?,
    @SerialName("dateCreated") override var dateCreated: String?,
    @SerialName("uuid") var uuid: String?,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject?,
    @SerialName("message") override val message: JsonElement?,
    @SerialName("messageMetaData") override val messageMetaData: MessageMetaData?,
    @SerialName("type") override val type: CampaignType = CampaignType.USNAT,
    @SerialName("url") override val url: String?,
    @SerialName("expirationDate") override val expirationDate: String?
) : CampaignMessage

@Serializable
data class GdprCS(
    val applies: Boolean?,
    @SerialName("categories") val categories: List<String>?,
    @SerialName("consentAllRef") val consentAllRef: String?,
    @SerialName("consentedToAll") val consentedToAll: Boolean?,
    @SerialName("legIntCategories") val legIntCategories: List<String>?,
    @SerialName("legIntVendors") val legIntVendors: List<String>?,
    @SerialName("postPayload") val postPayload: PostPayload?,
    @SerialName("rejectedAny") val rejectedAny: Boolean?,
    @SerialName("specialFeatures") val specialFeatures: List<String>?,
    @SerialName("vendors") val vendors: List<String>?,
    @SerialName("addtlConsent") val addtlConsent: String?,
    @SerialName("consentStatus") val consentStatus: ConsentStatus?,
    @SerialName("cookieExpirationDays") val cookieExpirationDays: Int?,
    @SerialName("customVendorsResponse") val customVendorsResponse: CustomVendorsResponse?,
    @SerialName("dateCreated") val dateCreated: String?,
    @SerialName("euconsent") val euconsent: String?,
    @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
    @Serializable(with = JsonMapSerializer::class) val TCData: Map<String, JsonElement>?,
    @SerialName("localDataCurrent") val localDataCurrent: Boolean?,
    @SerialName("uuid") var uuid: String?,
    @SerialName("vendorListId") val vendorListId: String?,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject? = null,
    @SerialName("expirationDate") var expirationDate: String?,
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
}
