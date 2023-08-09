package com.sourcepoint.cmplibrary.data.network.model.optimized.choice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChoiceMetaDataArg(
    @SerialName("applies")
    val applies: Boolean? = null,
)
