package com.sourcepoint.cmplibrary.gpp.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GppDataDto(
    @SerialName("MspaCoveredTransaction")
    val coveredTransaction: String,
    @SerialName("MspaOptOutOptionMode")
    val optOutOptionMode: String,
    @SerialName("MspaServiceProviderMode")
    val serviceProviderMode: String,
)
