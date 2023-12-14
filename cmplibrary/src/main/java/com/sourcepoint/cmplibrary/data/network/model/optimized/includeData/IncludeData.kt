package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

@Serializable
internal data class IncludeData(
    @SerialName("localState")
    val localState: IncludeDataParam? = null,
    @SerialName("TCData")
    val tcData: IncludeDataParam = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
    @SerialName("campaigns")
    val campaigns: IncludeDataParam? = null,
    @SerialName("customVendorsResponse")
    val customVendorsResponse: IncludeDataParam? = null,
    @SerialName("messageMetaData")
    val messageMetaData: IncludeDataParam? = null,
    @SerialName("webConsentPayload")
    val webConsentPayload: IncludeDataParam = IncludeDataParam(IncludeDataParamType.RECORD_STRING.type),
    @SerialName("GPPData")
    val gppData: Boolean = true,
    @SerialName("translateMessage")
    val translateMessage: Boolean = true,
    @SerialName("categories")
    val categories: Boolean = true,
)

internal fun IncludeData.encodeToString() = JsonConverter.converter.encodeToString(this)

enum class IncludeDataParamType(
    val type: String,
) {
    STRING("string"),
    RECORD_STRING("RecordString"),
}
