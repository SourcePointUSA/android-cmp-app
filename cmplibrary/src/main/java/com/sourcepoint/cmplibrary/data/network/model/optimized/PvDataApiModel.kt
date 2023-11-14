package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject

internal data class PvDataParamReq(
    val env: Env,
    val body: JsonObject,
    val campaignType: CampaignType
)

@Serializable
data class PvDataResp(
    @SerialName("ccpa") val ccpa: Campaign?,
    @SerialName("gdpr") val gdpr: Campaign?,
    @SerialName("usnat") val usnat: Campaign?,
) {
    @Serializable
    data class Campaign(
        @SerialName("uuid") val uuid: String?
    )

    override fun toString(): String {
        return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
            ?: super.toString()
    }
}
