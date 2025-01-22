package com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus

enum class GranularState {
    ALL,
    SOME,
    NONE,
    EMPTY_VL;

    companion object {
        fun firstWithStateOrNONE(state: String?) = (entries.firstOrNull { it.name == state })?: NONE
    }
}
