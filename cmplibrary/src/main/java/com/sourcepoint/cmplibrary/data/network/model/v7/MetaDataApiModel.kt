package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.DateSerializer
import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

internal data class MetaDataParamReq(
    val env: Env,
    val propertyId: Int,
    val accountId: Int,
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
}
