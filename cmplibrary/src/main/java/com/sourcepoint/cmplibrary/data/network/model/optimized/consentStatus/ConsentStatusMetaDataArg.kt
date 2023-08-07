package com.sourcepoint.cmplibrary.data.network.model.optimized.consentStatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ConsentStatusMetaDataArg(
    @SerialName("uuid")
    val uuid: String?,
    @SerialName("applies")
    val applies: Boolean? = null,
    @SerialName("hasLocalData")
    val hasLocalData: Boolean? = null,
    @SerialName("dateCreated")
    val dateCreated: String? = null,
)
