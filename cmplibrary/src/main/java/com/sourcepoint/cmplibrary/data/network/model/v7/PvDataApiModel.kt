package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject

internal data class PvDataParamReq(
    val env: Env,
    val body: JsonObject
)

@Serializable
data class PvDataResp(
    @SerialName("ccpa") val ccpa: Ccpa?,
    @SerialName("gdpr") val gdpr: Gdpr?
) {
    @Serializable
    data class Ccpa(
        @SerialName("cookies") val cookies: List<Cooky?>?,
        @SerialName("uuid") val uuid: String?
    )

    @Serializable
    data class Gdpr(
        @SerialName("cookies") val cookies: List<Cooky>?,
        @SerialName("uuid") val uuid: String?
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}
