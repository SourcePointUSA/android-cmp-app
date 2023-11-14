package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.MessageCategory
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement

@Serializable
internal data class MessagesParamReq(
    @SerialName("accountId") val accountId: Long,
    @SerialName("propertyId") val propertyId: Long,
    @SerialName("authId") val authId: String?,
    @SerialName("propertyHref") val propertyHref: String,
    @SerialName("env") val env: Env,
    @SerialName("metadataArg") val metadataArg: MetaDataArg?,
    @SerialName("body") val body: String,
    @SerialName("nonKeyedLocalState") val nonKeyedLocalState: JsonObject? = JsonObject(mapOf()),
    @SerialName("pubData") val pubData: JsonObject = JsonObject(mapOf()),
    @SerialName("localState") val localState: JsonObject? = JsonObject(mapOf()),
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
                campaigns?.usNat?.let { add(it) }
            }.associateBy { it.type.toCategoryId() }
            return priority.mapNotNull { list[it] }.toSet().toList()
        }

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull() ?: super.toString()
    }
}

interface CampaignMessage {
    val type: CampaignType
    val messageMetaData: MessageMetaData?
    val message: JsonElement?
    val url: String?
    val dateCreated: String?
    val expirationDate: String?
}

internal fun CampaignType.toCategoryId() = when (this) {
    CampaignType.GDPR -> 1
    CampaignType.CCPA -> 2
    CampaignType.USNAT -> 6
}

@Serializable
data class MessageMetaData(
    @SerialName("bucket") val bucket: Int?,
    @Serializable(with = MessageCategorySerializer::class) @SerialName("categoryId") val categoryId: MessageCategory,
    @SerialName("messageId") val messageId: Int?,
    @SerialName("msgDescription") val msgDescription: String?,
    @SerialName("prtnUUID") val prtnUUID: String?,
    @Serializable(with = MessageSubCategorySerializer::class) val subCategoryId: MessageSubCategory
) {
    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull() ?: "{}"
    }
}

@Serializable
data class Campaigns(
    @SerialName("CCPA") val ccpa: CCPA?,
    @SerialName("GDPR") val gdpr: GDPR?,
    @SerialName("usnat") val usNat: USNatConsentData?,
)

@Serializable
data class CCPA(
    @SerialName("consentedAll") val consentedAll: Boolean?,
    @SerialName("dateCreated") override val dateCreated: String?,
    @SerialName("message") override val message: JsonElement?,
    @SerialName("messageMetaData") override val messageMetaData: MessageMetaData?,
    @SerialName("newUser") val newUser: Boolean?,
    @SerialName("rejectedAll") val rejectedAll: Boolean?,
    @SerialName("rejectedCategories") val rejectedCategories: List<String>?,
    @SerialName("rejectedVendors") val rejectedVendors: List<String>?,
    @SerialName("signedLspa") val signedLspa: Boolean?,
    @SerialName("uspstring") val uspstring: String? = null,
    @SerialName("GPPData") @Serializable(with = JsonMapSerializer::class) val gppData: Map<String, JsonElement>? = null,
    @Serializable(with = CcpaStatusSerializer::class) val status: CcpaStatus?,
    @Serializable(with = CampaignTypeSerializer::class) override val type: CampaignType,
    @SerialName("url") override val url: String?,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject?,
    @SerialName("expirationDate") override val expirationDate: String?,
) : CampaignMessage

@Serializable
data class GDPR(
    @SerialName("addtlConsent") val addtlConsent: String?,
    @SerialName("childPmId") val childPmId: String?,
    @SerialName("consentStatus") val consentStatus: ConsentStatus?,
    @SerialName("customVendorsResponse") val customVendorsResponse: GdprCS.CustomVendorsResponse?,
    @SerialName("dateCreated") override val dateCreated: String?,
    @SerialName("euconsent") val euconsent: String?,
    @Serializable(with = GrantsSerializer::class) val grants: Map<String, GDPRPurposeGrants>?,
    @SerialName("hasLocalData") val hasLocalData: Boolean?,
    @SerialName("message") override val message: JsonElement?,
    @SerialName("messageMetaData") override val messageMetaData: MessageMetaData?,
    @Serializable(with = JsonMapSerializer::class) val TCData: Map<String, JsonElement>?,
    @Serializable(with = CampaignTypeSerializer::class) override val type: CampaignType,
    @SerialName("url") override val url: String?,
    @SerialName("expirationDate") override val expirationDate: String?,
    @SerialName("webConsentPayload") val webConsentPayload: JsonObject?,
) : CampaignMessage

@Serializable
data class ConsentStatus(
    @SerialName("consentedAll") var consentedAll: Boolean?,
    @SerialName("consentedToAny") val consentedToAny: Boolean?,
    @SerialName("granularStatus") val granularStatus: GranularStatus?,
    @SerialName("hasConsentData") val hasConsentData: Boolean?,
    @SerialName("rejectedAny") val rejectedAny: Boolean?,
    @SerialName("rejectedLI") val rejectedLI: Boolean?,
    @SerialName("legalBasisChanges") var legalBasisChanges: Boolean? = null,
    @SerialName("vendorListAdditions") var vendorListAdditions: Boolean? = null
) {
    @Serializable
    data class GranularStatus(
        @SerialName("defaultConsent") val defaultConsent: Boolean?,
        @SerialName("previousOptInAll") var previousOptInAll: Boolean?,
        @Serializable(with = GranularStateSerializer::class) val purposeConsent: GranularState?,
        @Serializable(with = GranularStateSerializer::class) val purposeLegInt: GranularState?,
        @Serializable(with = GranularStateSerializer::class) val vendorConsent: GranularState?,
        @Serializable(with = GranularStateSerializer::class) val vendorLegInt: GranularState?
    )
}

@Serializable
data class USNatConsentStatus(
    @SerialName("rejectedAny") val rejectedAny: Boolean?,
    @SerialName("consentedToAll") var consentedToAll: Boolean?,
    @SerialName("consentedToAny") val consentedToAny: Boolean?,
    @SerialName("granularStatus") val granularStatus: USNatGranularStatus?,
    @SerialName("hasConsentData") val hasConsentData: Boolean?,
) {
    @Serializable
    data class USNatGranularStatus(
        @SerialName("sellStatus") val sellStatus: Boolean?,
        @SerialName("shareStatus") val shareStatus: Boolean?,
        @SerialName("sensitiveDataStatus") val sensitiveDataStatus: Boolean?,
        @SerialName("gpcStatus") val gpcStatus: Boolean?,
        @SerialName("defaultConsent") val defaultConsent: Boolean?,
        @SerialName("previousOptInAll") var previousOptInAll: Boolean?,
        @SerialName("purposeConsent") var purposeConsent: String?,
    )
}

fun USNatConsentStatus.stringify(): JsonElement {
    return JsonConverter.converter.encodeToJsonElement(this)
}
