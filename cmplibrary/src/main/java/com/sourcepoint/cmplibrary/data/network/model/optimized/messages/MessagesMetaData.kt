package com.sourcepoint.cmplibrary.data.network.model.optimized.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MessagesMetaData(
    @SerialName("ccpa")
    val ccpa: MessagesMetaDataArg?,
    @SerialName("gdpr")
    val gdpr: MessagesMetaDataArg?,
)
