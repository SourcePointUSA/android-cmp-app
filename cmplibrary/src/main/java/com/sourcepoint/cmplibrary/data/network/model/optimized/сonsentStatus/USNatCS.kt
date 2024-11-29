package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverterImpl
import com.sourcepoint.cmplibrary.data.network.converter.JsonMapSerializer
import com.sourcepoint.cmplibrary.data.network.model.optimized.CampaignMessage
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessageMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentStatus
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ConsentableImpl
import com.sourcepoint.mobile_core.models.consents.USNatConsent
import com.sourcepoint.mobile_core.network.responses.ChoiceAllResponse
import com.sourcepoint.mobile_core.network.responses.USNatChoiceResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class USNatCS(
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
    val vendors: List<ConsentableImpl>
        get() { return userConsents?.vendors ?: emptyList() }
    val categories: List<ConsentableImpl>
        get() { return userConsents?.categories ?: emptyList() }

    fun copyingFrom(core: USNatConsent?, applies: Boolean?): USNatCS {
        if (core == null) { return this }

        return copy(
            applies = applies,
            dateCreated = core.dateCreated,
            expirationDate = core.expirationDate,
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
            consentStatus = USNatConsentStatus.initFrom(core.consentStatus)
        )
    }

    fun copyingFrom(core: ChoiceAllResponse.USNAT?, applies: Boolean?): USNatCS {
        if (core == null) { return this }

        return copy(
            applies = applies,
            consentStatus = USNatConsentStatus.initFrom(core.consentStatus),
            consentStrings = core.consentStrings.map {
                ConsentString(
                    sectionId = it.sectionId,
                    sectionName = it.sectionName,
                    consentString = it.consentString
                )
            },
            dateCreated = core.dateCreated,
            expirationDate = core.expirationDate,
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            gppData = core.gppData,
        )
    }

    fun copyingFrom(core: USNatChoiceResponse?): USNatCS {
        if (core == null) { return this }

        return copy(
            applies = applies,
            uuid = core.uuid,
            consentStatus = USNatConsentStatus.initFrom(core.consentStatus),
            consentStrings = core.consentStrings.map {
                ConsentString(
                    sectionId = it.sectionId,
                    sectionName = it.sectionName,
                    consentString = it.consentString
                )
            },
            userConsents = UserConsents(
                vendors = core.userConsents.vendors.map {
                    ConsentableImpl(
                        id = it.id,
                        consented = it.consented
                    )
                },
                categories = core.userConsents.categories.map {
                    ConsentableImpl(
                        id = it.id,
                        consented = it.consented
                    )
                },
            ),
            dateCreated = core.dateCreated,
            expirationDate = core.expirationDate,
            webConsentPayload = core.webConsentPayload?.let { JsonConverterImpl().toJsonObject(it) },
            gppData = core.gppData,
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
}
