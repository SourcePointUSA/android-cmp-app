package com.sourcepoint.cmplibrary.gpp.utils

import com.sourcepoint.cmplibrary.data.network.model.optimized.includeData.IncludeDataGppParam
import com.sourcepoint.cmplibrary.gpp.dto.GppData

internal fun GppData.toIncludeDataGppParam(): IncludeDataGppParam = IncludeDataGppParam(
    coveredTransaction = this.coveredTransaction.type,
    optOutOptionMode = this.optOutOptionMode.type,
    serviceProviderMode = this.serviceProviderMode.type,
)
