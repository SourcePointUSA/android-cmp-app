package com.sourcepoint.cmplibrary.gpp.utils

import com.sourcepoint.cmplibrary.gpp.dto.GppTernaryType

/**
 * Method that, if able, converts [String] to the [GppTernaryType]
 */
internal fun String.toGppTernaryType(): GppTernaryType = GppTernaryType.values()
    .find { it.type == this } ?: GppTernaryType.NON_APPLICABLE