package com.sourcepoint.cmplibrary.data.network.model.optimized.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MessagesMetaDataArg(
    @SerialName("applies")
    val applies: Boolean? = null,
)
