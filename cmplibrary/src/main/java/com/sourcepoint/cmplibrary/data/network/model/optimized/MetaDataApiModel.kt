package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement

@Serializable
internal data class MetaDataParamReq(
    @SerialName("env") val env: Env,
    @SerialName("propertyId") val propertyId: Long,
    @SerialName("accountId") val accountId: Long,
    @SerialName("metadata") val metadata: String
)

@Serializable
data class MetaDataResp(
    @SerialName("ccpa") val ccpa: Ccpa? = null,
    @SerialName("gdpr") val gdpr: Gdpr? = null,
    @SerialName("usnat") val usNat: USNat? = null,
) {
    @Serializable
    data class Ccpa(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("sampleRate") val sampleRate: Double?
    )

    @Serializable
    data class Gdpr(
        @SerialName("additionsChangeDate") val additionsChangeDate: String?,
        @SerialName("applies") val applies: Boolean?,
        @SerialName("getMessageAlways") val getMessageAlways: Boolean?,
        @SerialName("_id") val vendorListId: String?,
        @SerialName("legalBasisChangeDate") val legalBasisChangeDate: String?,
        @SerialName("version") val version: Int?,
        @SerialName("sampleRate") val sampleRate: Double?,
        @SerialName("childPmId") val childPmId: String?,
    )

    @Serializable
    data class USNat(
        @SerialName("_id") val vendorListId: String? = null,
        @SerialName("additionsChangeDate") val additionsChangeDate: String? = null,
        @SerialName("applies") val applies: Boolean? = null,
        @SerialName("sampleRate") val sampleRate: Double? = null,
        @SerialName("applicableSections") val applicableSections: JsonElement? = null,
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}
@Serializable
data class MetaDataMetaDataParam(
    val gdpr: MetaDataCampaign?,
    val ccpa: MetaDataCampaign?,
    val usnat: MetaDataCampaign?,
) {
    @Serializable
    data class MetaDataCampaign(val groupPmId: String?)
}

@Serializable
data class MetaDataArg(
    @SerialName("ccpa") val ccpa: CcpaArg?,
    @SerialName("gdpr") val gdpr: GdprArg?,
    @SerialName("usnat") val usNat: UsNatArg?,
) {
    @Serializable
    data class CcpaArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("hasLocalData") val hasLocalData: Boolean? = null,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null,
    )

    @Serializable
    data class GdprArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("hasLocalData") val hasLocalData: Boolean? = null,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null,
    )

    @Serializable
    data class UsNatArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("hasLocalData") val hasLocalData: Boolean? = null,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null,
        @SerialName("dateCreated") val dateCreated: String? = null,
        @SerialName("transitionCCPAAuth") val transitionCCPAAuth: Boolean? = null,
        @SerialName("optedOut") val optedOut: Boolean? = null,
    )
}

internal fun MetaDataResp.toMetaDataArg() = MetaDataArg(
    ccpa = MetaDataArg.CcpaArg(applies = ccpa?.applies),
    gdpr = MetaDataArg.GdprArg(applies = gdpr?.applies),
    usNat = MetaDataArg.UsNatArg(applies = usNat?.applies),
)
