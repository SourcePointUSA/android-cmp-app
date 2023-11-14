package com.sourcepoint.cmplibrary.data.network.model.optimized.choice

import com.sourcepoint.cmplibrary.data.network.model.optimized.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ChoiceResp(
    @SerialName("ccpa")
    val ccpa: CcpaCS? = null,
    @SerialName("gdpr")
    val gdpr: GdprCS? = null,
    @SerialName("usnat")
    val usNat: USNatConsentData? = null,
)
