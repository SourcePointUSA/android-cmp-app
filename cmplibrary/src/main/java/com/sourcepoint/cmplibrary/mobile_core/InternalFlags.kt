package com.sourcepoint.cmplibrary.mobile_core

import com.sourcepoint.mobile_core.models.SPInternalFlags

data class InternalFlags(val geoOverride: String? = null) {
    fun toCore() = SPInternalFlags(geoOverride = geoOverride)
}
