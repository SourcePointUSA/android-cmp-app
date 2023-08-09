package com.sourcepoint.cmplibrary.data.network.model.optimized.choice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChoiceMetaData(
    @SerialName("ccpa")
    val ccpa: ChoiceMetaDataArg?,
    @SerialName("gdpr")
    val gdpr: ChoiceMetaDataArg?,
)
