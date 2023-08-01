package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class IncludeDataParamType(
    @SerialName("type")
    val type: String,
) {
    STRING("string"),
    RECORD_STRING("RecordString"),
}
