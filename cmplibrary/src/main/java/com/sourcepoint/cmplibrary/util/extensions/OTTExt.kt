package com.sourcepoint.cmplibrary.util.extensions

import android.content.Context
import android.content.pm.PackageManager
import com.sourcepoint.cmplibrary.model.exposed.MessageType

internal fun Context.toMessageType(): MessageType =
    when (packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)) {
        true -> MessageType.OTT
        false -> MessageType.MOBILE
    }
