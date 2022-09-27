package com.sourcepoint.cmplibrary.data.network.model.v7


import com.sourcepoint.cmplibrary.data.network.converter.GrantsSerializer
import com.sourcepoint.cmplibrary.data.network.converter.TcDataSerializer
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Messages2(
    @SerialName("campaigns")
    val campaigns: Campaigns?,
    @SerialName("localState")
    val localState: LocalState?,
    @SerialName("nonKeyedLocalState")
    val nonKeyedLocalState: JsonElement?,
    @SerialName("priority")
    val priority: List<Int>,
    @SerialName("propertyId")
    val propertyId: Int?
) {
    interface CampaignMessage{
        val type: String?
        val messageMetaData: MessageMetaData
    }
    val campaignList : List<CampaignMessage>
        get() {
            val list = mutableListOf<CampaignMessage>().apply {
                campaigns?.gdpr?.let { add(it) }
                campaigns?.ccpa?.let { add(it) }
            }.associateBy { it.messageMetaData.categoryId }
            return priority.mapNotNull { list[it] }
        }

    @Serializable
    data class Campaigns(
        @SerialName("CCPA")
        val ccpa: CCPA?,
        @SerialName("GDPR")
        val gdpr: GDPR?
    ) {
        @Serializable
        data class CCPA(
            @SerialName("applies")
            val applies: Boolean?,
            @SerialName("consentedAll")
            val consentedAll: Boolean?,
            @SerialName("dateCreated")
            val dateCreated: String?,
            @SerialName("message")
            val message: JsonElement?,
            @SerialName("messageMetaData")
            override val messageMetaData: MessageMetaData,
            @SerialName("newUser")
            val newUser: Boolean?,
            @SerialName("rejectedAll")
            val rejectedAll: Boolean?,
            @SerialName("rejectedCategories")
            val rejectedCategories: List<String?>?,
            @SerialName("rejectedVendors")
            val rejectedVendors: List<String?>?,
            @SerialName("signedLspa")
            val signedLspa: Boolean?,
            @SerialName("status")
            val status: String?,
            @SerialName("type")
            override val type: String?,
            @SerialName("url")
            val url: String?,
            @SerialName("uspstring")
            val uspstring: String?
        ) : CampaignMessage {


        }

        @Serializable
        data class GDPR(
            @SerialName("actions")
            val actions: List<String?>?,
            @SerialName("addtlConsent")
            val addtlConsent: String?,
            @SerialName("childPmId")
            val childPmId: String?,
            @SerialName("consentStatus")
            val consentStatus: ConsentStatus?,
            @SerialName("customVendorsResponse")
            val customVendorsResponse: CustomVendorsResponse?,
            @SerialName("dateCreated")
            val dateCreated: String?,
            @SerialName("euconsent")
            val euconsent: String?,
            @Serializable(with = GrantsSerializer::class)
            val grants: Map<String, GDPRPurposeGrants>?,
            @SerialName("hasLocalData")
            val hasLocalData: Boolean?,
            @SerialName("message")
            val message: JsonElement?,
            @SerialName("messageMetaData")
            override val messageMetaData: MessageMetaData,
            @Serializable(with = TcDataSerializer::class)
            val TCData: Map<String, String>?,
            @SerialName("type")
            override val type: String?,
            @SerialName("url")
            val url: String?
        ) : CampaignMessage{
            @Serializable
            data class ConsentStatus(
                @SerialName("consentedAll")
                val consentedAll: Boolean?,
                @SerialName("consentedToAny")
                val consentedToAny: Boolean?,
                @SerialName("granularStatus")
                val granularStatus: GranularStatus?,
                @SerialName("hasConsentData")
                val hasConsentData: Boolean?,
                @SerialName("rejectedAny")
                val rejectedAny: Boolean?,
                @SerialName("rejectedLI")
                val rejectedLI: Boolean?
            ) {
                @Serializable
                data class GranularStatus(
                    @SerialName("defaultConsent")
                    val defaultConsent: Boolean?,
                    @SerialName("previousOptInAll")
                    val previousOptInAll: Boolean?,
                    @SerialName("purposeConsent")
                    val purposeConsent: String?,
                    @SerialName("purposeLegInt")
                    val purposeLegInt: String?,
                    @SerialName("vendorConsent")
                    val vendorConsent: String?,
                    @SerialName("vendorLegInt")
                    val vendorLegInt: String?
                )
            }

            @Serializable
            data class CustomVendorsResponse(
                @SerialName("consentedPurposes")
                val consentedPurposes: List<String?>?,
                @SerialName("consentedVendors")
                val consentedVendors: List<String?>?,
                @SerialName("legIntPurposes")
                val legIntPurposes: List<LegIntPurpose?>?
            ) {
                @Serializable
                data class LegIntPurpose(
                    @SerialName("_id")
                    val id: String?,
                    @SerialName("name")
                    val name: String?
                )
            }

        }
    }

    @Serializable
    data class MessageMetaData(
        @SerialName("bucket")
        val bucket: Int?,
        @SerialName("categoryId")
        val categoryId: Int?,
        @SerialName("messageId")
        val messageId: Int?,
        @SerialName("msgDescription")
        val msgDescription: String?,
        @SerialName("prtnUUID")
        val prtnUUID: String?,
        @SerialName("subCategoryId")
        val subCategoryId: Int?
    )

    @Serializable
    data class LocalState(
        @SerialName("ccpa")
        val ccpa: Ccpa?,
        @SerialName("gdpr")
        val gdpr: Gdpr?
    ) {
        @Serializable
        data class Ccpa(
            @SerialName("messageId")
            val messageId: Int?,
            @SerialName("mmsCookies")
            val mmsCookies: List<String?>?,
            @SerialName("propertyId")
            val propertyId: Int?
        )

        @Serializable
        data class Gdpr(
            @SerialName("messageId")
            val messageId: Int?,
            @SerialName("mmsCookies")
            val mmsCookies: List<String?>?,
            @SerialName("propertyId")
            val propertyId: Int?
        )
    }
}

