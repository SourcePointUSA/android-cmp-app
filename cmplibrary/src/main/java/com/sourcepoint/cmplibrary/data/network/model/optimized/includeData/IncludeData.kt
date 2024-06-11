package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.util.extensions.hasSupportForLegacyUSPString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.* //ktlint-disable

@Serializable
internal data class IncludeDataGppParam(
    @SerialName("MspaCoveredTransaction")
    val coveredTransaction: String? = null,
    @SerialName("MspaOptOutOptionMode")
    val optOutOptionMode: String? = null,
    @SerialName("MspaServiceProviderMode")
    val serviceProviderMode: String? = null,
    @SerialName("uspString")
    val uspString: Boolean? = null,
)

internal fun IncludeDataGppParam.encodeToString() = JsonConverter.converter.encodeToString(this)

internal fun buildIncludeData(spConfig: SpConfig) = buildJsonObject {

    putJsonObject("TCData") {
        put("type", "RecordString")
    }
    putJsonObject("campaigns") {
        put("type", "RecordString")
    }
    putJsonObject("webConsentPayload") {
        put("type", "RecordString")
    }
    putJsonObject("GPPData") {
        put("uspString", spConfig.hasSupportForLegacyUSPString())
    }
    put("categories", true)
}
