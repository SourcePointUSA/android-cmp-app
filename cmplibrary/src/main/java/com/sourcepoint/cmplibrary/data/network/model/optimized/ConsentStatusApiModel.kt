package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import java.time.Instant

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
        @SerialName("gdpr") val gdpr: GdprCS?
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}

@Serializable
data class CcpaCS(
    @SerialName("actions") val actions: List<Action>?,
    @SerialName("applies") val applies: Boolean?,
    @SerialName("ccpaApplies") val ccpaApplies: Boolean?,
    @SerialName("consentedAll") val consentedAll: Boolean?,
    @SerialName("cookies") val cookies: List<Cooky>?,
    @Serializable(with = DateSerializer::class) val dateCreated: Instant?,
    @SerialName("gpcEnabled") val gpcEnabled: Boolean?,
    @SerialName("newUser") val newUser: Boolean?,
    @SerialName("rejectedAll") val rejectedAll: Boolean?,
    @SerialName("rejectedCategories") val rejectedCategories: List<String>?,
    @SerialName("rejectedVendors") val rejectedVendors: List<String>?,
    @SerialName("signedLspa") val signedLspa: Boolean?,
    @Serializable(with = CcpaStatusSerializer::class) val status: CcpaStatus?,
    @SerialName("uspstring") val uspstring: String?,
    @SerialName("uuid") val uuid: String?
)

@Serializable
data class GdprCS(
    @SerialName("applies") val applies: Boolean?,
    @SerialName("gdprApplies") val gdprApplies: Boolean?,
    @SerialName("categories") val categories: List<String>?,
    @SerialName("consentAllRef") val consentAllRef: String?,
    @SerialName("consentedToAll") val consentedToAll: Boolean?,
    @SerialName("cookies") val cookies: List<Cooky>?,
    @SerialName("legIntCategories") val legIntCategories: List<String>?,
    @SerialName("legIntVendors") val legIntVendors: List<String>?,
    @SerialName("postPayload") val postPayload: PostPayload?,
    @SerialName("rejectedAny") val rejectedAny: Boolean?,
    @SerialName("specialFeatures") val specialFeatures: List<String>?,
    @SerialName("vendors") val vendors: List<String>?,
    @SerialName("addtlConsent") val addtlConsent: String?,
    @SerialName("consentStatus") val consentStatus: ConsentStatus?,
    @SerialName("consentUUID") val consentUUID: String?,
    @SerialName("cookieExpirationDays") val cookieExpirationDays: Int?,
    @SerialName("customVendorsResponse") val customVendorsResponse: CustomVendorsResponse?,
    @Serializable(with = DateSerializer::class) val dateCreated: Instant?,
    @SerialName("euconsent") val euconsent: String?,
    @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
    @Serializable(with = TcDataSerializer::class) val TCData: Map<String, String>?,
    @SerialName("localDataCurrent") val localDataCurrent: Boolean?,
    @SerialName("uuid") val uuid: String?,
    @SerialName("vendorListId") val vendorListId: String?,
    @SerialName("acceptedCategories") val acceptedCategories: List<String>?,
    @SerialName("acceptedVendors") val acceptedVendors: List<String>?,
) {

    @Serializable
    data class PostPayload(
        @SerialName("consentAllRef") val consentAllRef: String?,
        @SerialName("granularStatus") val granularStatus: com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus.GranularStatus,
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
    @SerialName("session") val session: Boolean?,
    @SerialName("shareRootDomain") val shareRootDomain: Boolean?,
    @SerialName("value") val value: String?,
    @SerialName("setPath") val setPath: Boolean?,
)
