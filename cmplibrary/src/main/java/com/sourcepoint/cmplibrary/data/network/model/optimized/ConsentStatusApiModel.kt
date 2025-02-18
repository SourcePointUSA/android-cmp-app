package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.ConsentableImpl
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.mobile_core.models.consents.CCPAConsent
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import com.sourcepoint.mobile_core.models.consents.USNatConsent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class ConsentStatusParamReq(
    @SerialName("env") val env: Env,
    @SerialName("metadata") val metadata: String,
    @SerialName("propertyId") val propertyId: Long,
    @SerialName("accountId") val accountId: Long,
    @SerialName("authId") val authId: String?,
    @SerialName("localState") val localState: JsonElement?,
    @SerialName("includeData") val includeData: JsonObject,
)

enum class GranularState {
    ALL,
    SOME,
    NONE,
    EMPTY_VL,
}

enum class GCMStatus(val status: String) {
    GRANTED("granted"),
    DENIED("denied");

    companion object {
        fun firstWithStatusOrNull(status: String?) = entries.firstOrNull { it.status == status }
    }
}

@Serializable
data class CcpaCS(
    val applies: Boolean? = null,
    @SerialName("consentedAll") val consentedAll: Boolean? = null,
    @SerialName("dateCreated") val dateCreated: String? = null,
    @SerialName("gpcEnabled") val gpcEnabled: Boolean? = null,
    @SerialName("newUser") val newUser: Boolean? = null,
    @SerialName("rejectedAll") val rejectedAll: Boolean? = null,
    @SerialName("rejectedCategories") val rejectedCategories: List<String>? = emptyList(),
    @SerialName("rejectedVendors") val rejectedVendors: List<String>? = emptyList(),
    @SerialName("signedLspa") val signedLspa: Boolean? = null,
    @SerialName("uspstring") val uspstring: String? = null,
    @Serializable(with = CcpaStatusSerializer::class) val status: CcpaStatus? = null,
    @SerialName("GPPData") @Serializable(with = JsonMapSerializer::class) val gppData: Map<String, JsonElement>? = emptyMap(),
    @SerialName("uuid") var uuid: String? = null,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject? = null,
    @SerialName("expirationDate") var expirationDate: String? = null,
) {
    fun copyingFrom(core: CCPAConsent?, applies: Boolean?): CcpaCS {
        if (core == null) { return this }

        return copy(
            applies = applies,
            rejectedVendors = core.rejectedVendors,
            rejectedCategories = core.rejectedCategories,
            signedLspa = core.signedLspa,
            status = CcpaStatus.entries.firstOrNull { it.name.lowercase() == core.status?.name?.lowercase() },
            dateCreated = null,//core.dateCreated,
            expirationDate = null,//core.expirationDate,
            uuid = core.uuid,
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            gppData = core.gppData
        )
    }
}

@Serializable
data class USNatConsentData(
    val applies: Boolean? = null,
    @SerialName("consentStatus") val consentStatus: USNatConsentStatus? = null,
    @SerialName("consentStrings") val consentStrings: List<ConsentString>? = null,
    @SerialName("dateCreated") override var dateCreated: String? = null,
    @SerialName("uuid") var uuid: String? = null,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject? = null,
    @SerialName("message") override val message: JsonElement? = null,
    @SerialName("GPPData") @Serializable(with = JsonMapSerializer::class) val gppData: Map<String, JsonElement>? = emptyMap(),
    @SerialName("messageMetaData") override val messageMetaData: MessageMetaData? = null,
    @SerialName("type") override val type: CampaignType = CampaignType.USNAT,
    @SerialName("url") override val url: String? = null,
    @SerialName("expirationDate") override val expirationDate: String? = null,
    val userConsents: UserConsents? = null
) : CampaignMessage {
    fun copyingFrom(core: USNatConsent?, applies: Boolean?): USNatConsentData {
        if (core == null) { return this }

        return copy(
            applies = applies,
            dateCreated = null,//core.dateCreated,
            expirationDate = null,//core.expirationDate,
            uuid = core.uuid,
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            gppData = core.gppData,
            consentStrings = core.consentStrings.map {
                ConsentString(
                    sectionId = it.sectionId,
                    sectionName = it.sectionName,
                    consentString = it.consentString
                )
            },
            consentStatus = USNatConsentStatus(
                rejectedAny = core.consentStatus.rejectedAny,
                consentedToAll = core.consentStatus.consentedToAll,
                consentedToAny = core.consentStatus.consentedToAny,
                vendorListAdditions = core.consentStatus.vendorListAdditions,
                hasConsentData = core.consentStatus.hasConsentData,
                granularStatus = USNatConsentStatus.USNatGranularStatus(
                    sellStatus = core.consentStatus.granularStatus?.sellStatus,
                    shareStatus = core.consentStatus.granularStatus?.shareStatus,
                    sensitiveDataStatus = core.consentStatus.granularStatus?.sensitiveDataStatus,
                    defaultConsent = core.consentStatus.granularStatus?.defaultConsent,
                    gpcStatus = core.consentStatus.granularStatus?.gpcStatus,
                    previousOptInAll = core.consentStatus.granularStatus?.previousOptInAll,
                    purposeConsent = core.consentStatus.granularStatus?.purposeConsent
                )
            )
        )
    }

    @Serializable
    data class ConsentString(
        @SerialName("sectionId") val sectionId: Int?,
        @SerialName("sectionName") val sectionName: String?,
        @SerialName("consentString") val consentString: String?
    )

    @Serializable
    data class UserConsents(
        val vendors: List<ConsentableImpl>? = emptyList(),
        val categories: List<ConsentableImpl>? = emptyList()
    )

    val vendors: List<ConsentableImpl>
        get() { return userConsents?.vendors ?: emptyList() }
    val categories: List<ConsentableImpl>
        get() { return userConsents?.categories ?: emptyList() }
}

@Serializable
data class GdprCS(
    val applies: Boolean? = null,
    @SerialName("categories") val categories: List<String>? = emptyList(),
    @SerialName("consentAllRef") val consentAllRef: String? = null,
    @SerialName("consentedToAll") val consentedToAll: Boolean? = null,
    @SerialName("legIntCategories") val legIntCategories: List<String>? = emptyList(),
    @SerialName("legIntVendors") val legIntVendors: List<String>? = emptyList(),
    @SerialName("postPayload") val postPayload: PostPayload? = null,
    @SerialName("rejectedAny") val rejectedAny: Boolean? = null,
    @SerialName("specialFeatures") val specialFeatures: List<String>? = emptyList(),
    @SerialName("vendors") val vendors: List<String>? = emptyList(),
    @SerialName("addtlConsent") val addtlConsent: String? = null,
    @SerialName("consentStatus") val consentStatus: ConsentStatus? = null,
    @SerialName("cookieExpirationDays") val cookieExpirationDays: Int? = null,
    @SerialName("customVendorsResponse") val customVendorsResponse: CustomVendorsResponse? = null,
    @SerialName("dateCreated") val dateCreated: String? = null,
    @SerialName("euconsent") val euconsent: String? = null,
    @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>? = emptyMap(),
    @Serializable(with = JsonMapSerializer::class) val TCData: Map<String, JsonElement>? = emptyMap(),
    @SerialName("localDataCurrent") val localDataCurrent: Boolean? = null,
    @SerialName("uuid") var uuid: String? = null,
    @SerialName("vendorListId") val vendorListId: String? = null,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject? = null,
    @SerialName("expirationDate") var expirationDate: String? = null,
    @SerialName("gcmStatus") var googleConsentMode: GoogleConsentMode? = null,
) {
    fun copyingFrom(core: GDPRConsent?, applies: Boolean?): GdprCS {
        if (core == null) { return this }

        return copy(
            applies = applies,
            categories = core.categories,
            legIntCategories = core.legIntCategories,
            legIntVendors = core.legIntVendors,
            specialFeatures = core.specialFeatures,
            vendors = core.vendors,
            dateCreated = null,//core.dateCreated,
            expirationDate = null,//core.expirationDate,
            euconsent = core.euconsent,
            uuid = core.uuid,
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            googleConsentMode = core.gcmStatus?.let { gcm ->
                GoogleConsentMode(
                    adStorage = GCMStatus.firstWithStatusOrNull(gcm.adStorage),
                    analyticsStorage = GCMStatus.firstWithStatusOrNull(gcm.analyticsStorage),
                    adUserData = GCMStatus.firstWithStatusOrNull(gcm.adUserData),
                    adPersonalization = GCMStatus.firstWithStatusOrNull(gcm.adPersonalization),
                )
            },
            grants = core.grants.mapValues {
                GDPRPurposeGrants(
                    granted = it.value.vendorGrant,
                    purposeGrants = it.value.purposeGrants
                )
            },
            TCData = core.tcData
        )
    }

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
