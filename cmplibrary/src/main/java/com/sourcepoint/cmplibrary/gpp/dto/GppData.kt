package com.sourcepoint.cmplibrary.gpp.dto

internal data class GppData(
    val coveredTransaction: GppBinaryType = GppBinaryType.NO,
    val optOutOptionMode: GppTernaryType = GppTernaryType.NOT_APPLICABLE,
    val serviceProviderMode: GppTernaryType = GppTernaryType.NOT_APPLICABLE,
)
