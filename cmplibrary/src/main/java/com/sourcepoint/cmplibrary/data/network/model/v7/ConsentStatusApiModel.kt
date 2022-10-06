package com.sourcepoint.cmplibrary.data.network.model.v7

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

internal data class ConsentStatusParamReq(
    val env: Env,
    val metadata: String,
    val propertyId: Long,
    val accountId: Long,
    val authId: String?
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
    ) {
        @Serializable
        data class CcpaCS(
            @SerialName("ccpaApplies") val ccpaApplies: Boolean?,
            @SerialName("consentedAll") val consentedAll: Boolean?,
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
            @SerialName("addtlConsent") val addtlConsent: String?,
            @SerialName("consentStatus") val consentStatus: ConsentStatus?,
            @SerialName("consentUUID") val consentUUID: String?,
            @SerialName("cookieExpirationDays") val cookieExpirationDays: Int?,
            @SerialName("cookies") val cookies: List<Cooky?>?,
            @SerialName("customVendorsResponse") val customVendorsResponse: CustomVendorsResponse?,
            @Serializable(with = DateSerializer::class) val dateCreated: Instant?,
            @SerialName("euconsent") val euconsent: String?,
            @SerialName("gdprApplies") val gdprApplies: Boolean?,
            @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
            @SerialName("localDataCurrent") val localDataCurrent: Boolean?,
            @SerialName("uuid") val uuid: String?,
            @SerialName("vendorListId") val vendorListId: String?
        ) {
            @Serializable
            data class Action(
                @SerialName("_id") val id: String?,
                @SerialName("js") val js: String?,
                @SerialName("tagManager") val tagManager: TagManager?,
                @SerialName("type") val type: String?,
                @SerialName("url") val url: String?
            ) {
                @Serializable
                data class TagManager(
                    @SerialName("_id") val id: String?,
                    @SerialName("key") val key: String?,
                    @SerialName("name") val name: String?,
                    @SerialName("value") val value: String?
                )
            }

//            @Serializable
//            data class ConsentStatus(
//                @SerialName("consentedAll") val consentedAll: Boolean?,
//                @SerialName("consentedToAny") val consentedToAny: Boolean?,
//                @SerialName("granularStatus") val granularStatus: GranularStatus?,
//                @SerialName("hasConsentData") val hasConsentData: Boolean?,
//                @SerialName("rejectedAny") val rejectedAny: Boolean?,
//                @SerialName("rejectedLI") val rejectedLI: Boolean?
//            ) {
//                @Serializable
//                data class GranularStatus(
//                    @SerialName("defaultConsent") var defaultConsent: Boolean?,
//                    @SerialName("previousOptInAll") var previousOptInAll: Boolean?,
//                    @Serializable(with = GranularStateSerializer::class) val purposeConsent: GranularState?,
//                    @Serializable(with = GranularStateSerializer::class) val purposeLegInt: GranularState?,
//                    @Serializable(with = GranularStateSerializer::class) val vendorConsent: GranularState?,
//                    @Serializable(with = GranularStateSerializer::class) val vendorLegInt: GranularState?
//                )
//            }

            @Serializable
            data class Cooky(
                @SerialName("key") val key: String?,
                @SerialName("maxAge") val maxAge: Int?,
                @SerialName("session") val session: Boolean?,
                @SerialName("shareRootDomain") val shareRootDomain: Boolean?,
                @SerialName("value") val value: String?
            )

            @Serializable
            data class CustomVendorsResponse(
                @SerialName("consentedPurposes") val consentedPurposes: List<ConsentedPurpose?>?,
                @SerialName("consentedVendors") val consentedVendors: List<ConsentedVendor?>?,
                @SerialName("legIntPurposes") val legIntPurposes: List<LegIntPurpose?>?
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
    }

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}
