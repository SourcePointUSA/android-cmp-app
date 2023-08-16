package com.sourcepoint.cmplibrary.gpp.utils

import com.sourcepoint.cmplibrary.gpp.dto.GppBinaryType

/**
 * Method that, if able, converts [String] to the [GppBinaryType]
 */
internal fun String.toGppBinaryType(): GppBinaryType = GppBinaryType.values()
    .find { it.type == this } ?: GppBinaryType.NO
