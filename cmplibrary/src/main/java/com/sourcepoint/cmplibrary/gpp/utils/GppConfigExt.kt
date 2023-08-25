package com.sourcepoint.cmplibrary.gpp.utils

import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeDataGppParam
import com.sourcepoint.cmplibrary.exposed.gpp.SpGppConfig

internal fun SpGppConfig?.toIncludeDataGppParam(): IncludeDataGppParam = IncludeDataGppParam(
    coveredTransaction = this?.coveredTransaction?.type,
    optOutOptionMode = this?.optOutOptionMode?.type,
    serviceProviderMode = this?.serviceProviderMode?.type,
)
