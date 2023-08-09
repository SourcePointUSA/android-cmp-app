package com.sourcepoint.cmplibrary.data.network.model.optimized.consentStatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ConsentStatusMetaData(
    @SerialName("ccpa")
    val ccpa: ConsentStatusMetaDataArg?,
    @SerialName("gdpr")
    val gdpr: ConsentStatusMetaDataArg?,
)
