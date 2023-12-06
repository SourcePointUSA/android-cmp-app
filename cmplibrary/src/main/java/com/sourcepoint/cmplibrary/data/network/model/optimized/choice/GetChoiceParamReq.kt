package com.sourcepoint.cmplibrary.data.network.model.optimized.choice

import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataArg
import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeData
import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.Serializable

@Serializable
internal data class GetChoiceParamReq(
    val env: Env,
    val choiceType: ChoiceTypeParam,
    val metadataArg: MetaDataArg?,
    val propertyId: Long,
    val accountId: Long,
    val includeData: IncludeData = IncludeData(),
    val hasCsp: Boolean = true,
    val includeCustomVendorsRes: Boolean = false,
    val withSiteActions: Boolean = false,
)
