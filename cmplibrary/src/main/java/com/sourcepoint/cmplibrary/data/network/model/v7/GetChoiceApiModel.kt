package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

internal class ChoiceParamReq(
    val env: Env,
    val choiceType: ChoiceTypeParam,
    val metadataArg: MetaDataArg?,
    val propertyId: Long,
    val accountId: Long,
    val body: JsonObject = JsonObject(mapOf())
)

enum class ChoiceTypeParam(val type: String) {
    CONSENT_ALL("consent-all"),
    REJECT_ALL("reject-all")
}

@Serializable
data class ChoiceResp(
    @SerialName("ccpa") val ccpa: CcpaCS?,
    @SerialName("gdpr") val gdpr: GdprCS?
)
