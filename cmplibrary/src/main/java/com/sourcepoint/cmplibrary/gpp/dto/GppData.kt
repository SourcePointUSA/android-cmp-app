package com.sourcepoint.cmplibrary.gpp.dto

internal data class GppData(
    val coveredTransaction: GppBinaryType = GppBinaryType.NO,
    val optOutOptionMode: GppTernaryType = GppTernaryType.NON_APPLICABLE,
    val serviceProviderMode: GppTernaryType = GppTernaryType.NON_APPLICABLE,
)
