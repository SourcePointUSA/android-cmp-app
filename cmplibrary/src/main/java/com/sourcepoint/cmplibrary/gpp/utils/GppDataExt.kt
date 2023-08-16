package com.sourcepoint.cmplibrary.gpp.utils

import com.sourcepoint.cmplibrary.gpp.dto.GppData
import com.sourcepoint.cmplibrary.gpp.dto.GppDataDto

internal fun GppDataDto.toGppData(): GppData = GppData(
    coveredTransaction = this.coveredTransaction.toGppBinaryType(),
    optOutOptionMode = this.optOutOptionMode.toGppTernaryType(),
    serviceProviderMode = this.serviceProviderMode.toGppTernaryType(),
)

internal fun GppData.toGppDataDto(): GppDataDto = GppDataDto(
    coveredTransaction = this.coveredTransaction.type,
    optOutOptionMode = this.optOutOptionMode.type,
    serviceProviderMode = this.serviceProviderMode.type,
)