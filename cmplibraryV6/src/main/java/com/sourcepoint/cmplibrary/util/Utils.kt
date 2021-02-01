package com.sourcepoint.cmplibrary.util

import android.os.Looper
import com.sourcepoint.gdpr_cmplibrary.exception.ExecutionOutOfMainThreadException

internal fun checkMainThread(cMethodName: String) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        throw ExecutionOutOfMainThreadException(description = "$cMethodName must be called from the MainThread")
    }
}
