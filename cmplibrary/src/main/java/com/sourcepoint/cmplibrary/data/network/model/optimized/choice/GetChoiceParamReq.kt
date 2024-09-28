package com.sourcepoint.cmplibrary.data.network.model.optimized.choice

import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
internal data class GetChoiceParamReq(
    val env: Env,
    val choiceType: ChoiceTypeParam,
    val metadataArg: MetaData?,
    val propertyId: Long,
    val accountId: Long,
    val includeData: JsonObject,
    val hasCsp: Boolean = true,
    val includeCustomVendorsRes: Boolean = false,
    val withSiteActions: Boolean = false,
) {
    @Serializable
    data class MetaData(
        val gdpr: Campaign? = null,
        val ccpa: Campaign? = null,
        val usnat: Campaign? = null
    ) {
        @Serializable
        data class Campaign(val applies: Boolean)
    }
}
