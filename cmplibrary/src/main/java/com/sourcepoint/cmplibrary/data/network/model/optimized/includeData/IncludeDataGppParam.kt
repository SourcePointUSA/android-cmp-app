package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class IncludeDataGppParam(
    @SerialName("MspaCoveredTransaction")
    val coveredTransaction: String? = null,
    @SerialName("MspaOptOutOptionMode")
    val optOutOptionMode: String? = null,
    @SerialName("MspaServiceProviderMode")
    val serviceProviderMode: String? = null,
)
