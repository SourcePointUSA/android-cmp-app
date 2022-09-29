package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.MessageCategory
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

internal data class MessagesParamReq(
    val env: Env,
    val metadata: String,
    val body: String,
    val nonKeyedLocalState: String
)

@Serializable
data class MessagesResp(
    @SerialName("campaigns") val campaigns: Campaigns?,
    @SerialName("localState") val localState: JsonElement?,
    @SerialName("nonKeyedLocalState") val nonKeyedLocalState: JsonElement?,
    @SerialName("priority") val priority: List<Int>,
    @SerialName("propertyId") val propertyId: Int?
) {
    val campaignList: List<CampaignMessage>
        get() {
            val list = mutableListOf<CampaignMessage>().apply {
                campaigns?.gdpr?.let { add(it) }
                campaigns?.ccpa?.let { add(it) }
            }.associateBy { it.messageMetaData.categoryId.code }
            return priority.mapNotNull { list[it] }
        }
}

interface CampaignMessage {
    val type: String?
    val messageMetaData: MessageMetaData
    val message: JsonElement?
    val url: String?
    val dateCreated: String?
}

@Serializable
data class MessageMetaData(
    @SerialName("bucket") val bucket: Int?,
    @Serializable(with = MessageCategorySerializer::class)
    @SerialName("categoryId") val categoryId: MessageCategory,
    @SerialName("messageId") val messageId: Int?,
    @SerialName("msgDescription") val msgDescription: String?,
    @SerialName("prtnUUID") val prtnUUID: String?,
    @Serializable(with = MessageSubCategorySerializer::class) val subCategoryId: MessageSubCategory?
)

@Serializable
data class Campaigns(
    @SerialName("CCPA") val ccpa: CCPA?,
    @SerialName("GDPR") val gdpr: GDPR?
)

@Serializable
data class CCPA(
    @SerialName("applies") val applies: Boolean?,
    @SerialName("consentedAll") val consentedAll: Boolean?,
    @SerialName("dateCreated") override val dateCreated: String?,
    @SerialName("message") override val message: JsonElement?,
    @SerialName("messageMetaData") override val messageMetaData: MessageMetaData,
    @SerialName("newUser") val newUser: Boolean?,
    @SerialName("rejectedAll") val rejectedAll: Boolean?,
    @SerialName("rejectedCategories") val rejectedCategories: List<String?>?,
    @SerialName("rejectedVendors") val rejectedVendors: List<String?>?,
    @SerialName("signedLspa") val signedLspa: Boolean?,
    @Serializable(with = CcpaStatusSerializer::class) val status: CcpaStatus?,
    @SerialName("type") override val type: String?,
    @SerialName("url") override val url: String?,
    @SerialName("uspstring") val uspstring: String?
) : CampaignMessage

@Serializable
data class GDPR(
    @SerialName("actions") val actions: List<String?>?,
    @SerialName("addtlConsent") val addtlConsent: String?,
    @SerialName("childPmId") val childPmId: String?,
    @SerialName("consentStatus") val consentStatus: ConsentStatus?,
    @SerialName("customVendorsResponse") val customVendorsResponse: CustomVendorsResponse?,
    @SerialName("dateCreated") override val dateCreated: String?,
    @SerialName("euconsent") val euconsent: String?,
    @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
    @SerialName("hasLocalData") val hasLocalData: Boolean?,
    @SerialName("message") override val message: JsonElement?,
    @SerialName("messageMetaData") override val messageMetaData: MessageMetaData,
    @Serializable(with = TcDataSerializer::class) val TCData: Map<String, String>?,
    @SerialName("type") override val type: String?,
    @SerialName("url") override val url: String?
) : CampaignMessage {
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

    @Serializable
    data class CustomVendorsResponse(
        @SerialName("consentedPurposes") val consentedPurposes: List<String?>?,
        @SerialName("consentedVendors") val consentedVendors: List<String?>?,
        @SerialName("legIntPurposes") val legIntPurposes: List<LegIntPurpose?>?
    ) {
        @Serializable
        data class LegIntPurpose(
            @SerialName("_id") val id: String?,
            @SerialName("name") val name: String?
        )
    }
}
