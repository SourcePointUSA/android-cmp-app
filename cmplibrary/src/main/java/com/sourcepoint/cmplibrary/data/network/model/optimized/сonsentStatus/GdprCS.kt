package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

import com.sourcepoint.cmplibrary.data.network.converter.GrantsSerializer
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverterImpl
import com.sourcepoint.cmplibrary.data.network.converter.JsonMapSerializer
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.GoogleConsentMode
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.json.JSONObject

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
            dateCreated = core.dateCreated,
            expirationDate = core.expirationDate,
            euconsent = core.euconsent,
            uuid = core.uuid,
            consentStatus = ConsentStatus.initFrom(core.consentStatus),
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            googleConsentMode = core.gcmStatus?.let { gcm -> GoogleConsentMode.initFrom(gcm) },
            grants = core.grants.mapValues {
                GDPRPurposeGrants(
                    granted = it.value.vendorGrant,
                    purposeGrants = it.value.purposeGrants
                )
            },
            TCData = core.tcData
        )
    }

    internal fun toGDPRUserConsent(): GDPRConsentInternal {
        return GDPRConsentInternal(
            uuid = uuid,
            applies = applies ?: false,
            tcData = TCData?.toMapOfAny() ?: emptyMap(),
            grants = grants ?: emptyMap(),
            euconsent = euconsent ?: "",
            acceptedCategories = categories,
            consentStatus = consentStatus,
            childPmId = null,
            thisContent = JSONObject(),
            webConsentPayload = webConsentPayload,
            googleConsentMode = googleConsentMode
        )
    }

    fun toCoreGDPRConsent(): GDPRConsent {
        return GDPRConsent(
            applies = applies ?: false,
            dateCreated = dateCreated,
            expirationDate = expirationDate,
            uuid = uuid,
            childPmId = "",
            euconsent = euconsent,
            legIntCategories = legIntCategories?: emptyList(),
            legIntVendors = legIntVendors?: emptyList(),
            vendors = vendors?: emptyList(),
            categories = categories?: emptyList(),
            specialFeatures = specialFeatures?: emptyList(),
            grants = grants?.mapValues {
                GDPRConsent.VendorGrantsValue(
                    vendorGrant = it.value.granted,
                    purposeGrants = it.value.purposeGrants
                )
            }?: emptyMap(),
            gcmStatus = googleConsentMode?.toCoreGCMStatus(),
            webConsentPayload = webConsentPayload.toString(),
            consentStatus = consentStatus?.toCoreConsentStatus() ?: com.sourcepoint.mobile_core.models.consents.ConsentStatus(),
            tcData = TCData?.mapValues { it.value.jsonPrimitive } ?: emptyMap()
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
