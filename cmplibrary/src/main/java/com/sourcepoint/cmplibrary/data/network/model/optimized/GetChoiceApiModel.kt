package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.json.JSONObject

@Serializable
internal class ChoiceParamReq(
    val env: Env,
    val choiceType: ChoiceTypeParam,
    val metadataArg: MetaDataArg?,
    val propertyId: Long,
    val accountId: Long,
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

internal fun ChoiceParamReq.toJsonObject(): JSONObject {
    return JSONObject().apply {
        put("env", env.name)
        put("choiceType", choiceType.type)
        put("metadataArg", check { JsonConverter.converter.encodeToString(metadataArg) }.getOrNull())
        put("propertyId", propertyId)
        put("accountId", accountId)
    }
}
