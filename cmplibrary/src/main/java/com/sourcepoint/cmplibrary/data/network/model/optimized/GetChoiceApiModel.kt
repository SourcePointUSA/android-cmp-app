package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class ChoiceParamReq(
    val env: Env,
    val choiceType: ChoiceTypeParam,
    val metadataArg: MetaDataArg?,
    val propertyId: Long,
    val accountId: Long
)

enum class ChoiceTypeParam(val type: String) {
    CONSENT_ALL("consent-all"),
    REJECT_ALL("reject-all")
}

@Serializable
data class ChoiceResp(
    @SerialName("ccpa") val ccpa: CcpaCS? = null,
    @SerialName("gdpr") val gdpr: GdprCS? = null
)
