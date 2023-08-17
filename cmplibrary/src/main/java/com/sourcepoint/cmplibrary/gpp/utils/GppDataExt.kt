package com.sourcepoint.cmplibrary.gpp.utils

import com.sourcepoint.cmplibrary.gpp.dto.GppData
import com.sourcepoint.cmplibrary.gpp.dto.GppDataDto

internal fun GppData.toGppDataDto(): GppDataDto = GppDataDto(
    coveredTransaction = this.coveredTransaction.type,
    optOutOptionMode = this.optOutOptionMode.type,
    serviceProviderMode = this.serviceProviderMode.type,
)
