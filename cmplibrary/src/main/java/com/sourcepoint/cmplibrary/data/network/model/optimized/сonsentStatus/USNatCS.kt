package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverterImpl
import com.sourcepoint.cmplibrary.data.network.converter.JsonMapSerializer
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.CampaignMessage
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessageMetaData
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentStatus
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ConsentableImpl
import com.sourcepoint.cmplibrary.model.exposed.UsNatConsentInternal
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
import com.sourcepoint.mobile_core.models.consents.USNatConsent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.mobile_core.models.consents.ConsentStatus
import com.sourcepoint.mobile_core.models.consents.USNatConsent.USNatUserConsents
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonPrimitive


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
        get() = userConsents?.vendors ?: emptyList()
    val categories: List<ConsentableImpl>
        get() = userConsents?.categories ?: emptyList()

    fun toCoreUSNatConsent(): USNatConsent = USNatConsent(
        applies = applies?: false,
        dateCreated = dateCreated,
        expirationDate = expirationDate,
        uuid = uuid,
        webConsentPayload = webConsentPayload.toString(),
        consentStatus = consentStatus?.toCoreConsentStatus() ?: ConsentStatus(),
        consentStrings = consentStrings?.filter {
            it.consentString != null && it.sectionName != null && it.sectionId != null
        }?.map {
            USNatConsent.USNatConsentSection(
                sectionId = it.sectionId!!,
                sectionName = it.sectionName!!,
                consentString = it.consentString!!
            )
        } ?: emptyList(),
        userConsents = userConsents?.toCoreUSNatUserConsents() ?: USNatUserConsents(),
        gppData = gppData?.mapValues { it.value.jsonPrimitive } ?: emptyMap()
    )

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

    internal fun toUsNatConsentInternal(): UsNatConsentInternal = UsNatConsentInternal(
        applies = applies ?: false,
        gppData = gppData?.toMapOfAny() ?: emptyMap(),
        consentStatus = consentStatus,
        vendors = vendors,
        categories = categories,
        consentStrings = consentStrings ?: emptyList(),
        dateCreated = dateCreated,
        uuid = uuid,
        webConsentPayload = webConsentPayload,
        url = url,
    )

    internal fun stringify(): String? =
        check { JsonConverter.converter.encodeToString(this) }.getOrNull()

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
    ) {
        fun toCoreUSNatUserConsents(): USNatUserConsents = USNatUserConsents(
            vendors = vendors?.map { USNatConsent.USNatConsentable(it.id, it.consented) } ?: emptyList(),
            categories = categories?.map { USNatConsent.USNatConsentable(it.id, it.consented) } ?: emptyList()
        )
    }
}
