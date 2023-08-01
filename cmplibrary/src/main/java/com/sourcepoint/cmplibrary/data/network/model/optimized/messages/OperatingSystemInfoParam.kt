package com.sourcepoint.cmplibrary.data.network.model.optimized.messages

import android.os.Build
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OperatingSystemInfoParam(
    @SerialName("name")
    val name: String? = DEFAULT_ANDROID_OS_NAME,
    @SerialName("version")
    val version: String? = Build.VERSION.SDK_INT.toString(),
) {

    companion object {
        private const val DEFAULT_ANDROID_OS_NAME = "android"
    }
}
