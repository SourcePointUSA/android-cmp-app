package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class MetaDataParamReq(
    val env: Env,
    val propertyId: Int,
    val accountId: Int,
    val metadata: MetaDataMetaDataParam
) {
    @Serializable
    data class MetaDataMetaDataParam(
        val gdpr: MetaDataCampaign?,
        val ccpa: MetaDataCampaign?
    ) {
        @Serializable
        data class MetaDataCampaign(val groupPmId: String?)
    }
}

@Serializable
data class MetaDataResp(
    @SerialName("ccpa") val ccpa: Ccpa?,
    @SerialName("gdpr") val gdpr: Gdpr?
) {
    @Serializable
    data class Ccpa(
        @SerialName("applies") val applies: Boolean,
        @SerialName("sampleRate") val sampleRate: Double
    )

    @Serializable
    data class Gdpr(
        @SerialName("additionsChangeDate") val additionsChangeDate: String,
        @SerialName("applies") val applies: Boolean,
        @SerialName("_id") val id: String,
        @SerialName("legalBasisChangeDate") val legalBasisChangeDate: String,
        @SerialName("version") val version: Int,
        @SerialName("sampleRate") val sampleRate: Double,
        @SerialName("childPmId") val childPmId: String?,
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}
