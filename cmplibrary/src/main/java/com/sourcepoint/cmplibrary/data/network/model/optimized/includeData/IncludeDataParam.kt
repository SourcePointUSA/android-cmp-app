package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IncludeDataParam(
    @SerialName("type")
    val type: String,
)
