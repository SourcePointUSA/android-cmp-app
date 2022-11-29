package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import java.time.Instant

internal data class MetaDataParamReq(
    val env: Env,
    val propertyId: Long,
    val accountId: Long,
    val metadata: String
)

@Serializable
data class MetaDataResp(
    @SerialName("ccpa") val ccpa: Ccpa?,
    @SerialName("gdpr") val gdpr: Gdpr?
) {
    @Serializable
    data class Ccpa(
        @SerialName("applies") val applies: Boolean?
    )

    @Serializable
    data class Gdpr(
        @Serializable(with = DateSerializer::class) val additionsChangeDate: Instant?,
        @SerialName("applies") val applies: Boolean?,
        @SerialName("getMessageAlways") val getMessageAlways: Boolean?,
        @SerialName("_id") val id: String?,
        @Serializable(with = DateSerializer::class) val legalBasisChangeDate: Instant?,
        @SerialName("version") val version: Int?
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}

@Serializable
data class MetaDataArg(
    @SerialName("ccpa") val ccpa: CcpaArg?,
    @SerialName("gdpr") val gdpr: GdprArg?
) {
    @Serializable
    data class CcpaArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null
    )

    @Serializable
    data class GdprArg(
        @SerialName("applies") val applies: Boolean?,
        @SerialName("groupPmId") val groupPmId: String? = null,
        @SerialName("targetingParams") val targetingParams: JsonElement? = null,
        @SerialName("uuid") val uuid: String? = null
    )
}

internal fun MetaDataResp.toMetaDataArg() = MetaDataArg(
    ccpa = MetaDataArg.CcpaArg(applies = ccpa?.applies),
    gdpr = MetaDataArg.GdprArg(
        applies = gdpr?.applies
    )
)