package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

enum class GCMStatus(val status: String) {
    GRANTED("granted"),
    DENIED("denied");

    companion object {
        fun firstWithStatusOrNull(status: String?) = entries.firstOrNull { it.status == status }
    }
}
