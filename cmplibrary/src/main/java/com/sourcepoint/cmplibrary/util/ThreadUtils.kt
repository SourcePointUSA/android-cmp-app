package com.sourcepoint.cmplibrary.util

import android.os.Looper
import com.sourcepoint.cmplibrary.exception.ExecutionInTheWrongThreadException

internal fun checkMainThread(cMethodName: String) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        throw ExecutionInTheWrongThreadException(description = "$cMethodName must be called from the MainThread")
    }
}

internal fun checkWorkerThread(cMethodName: String) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        throw ExecutionInTheWrongThreadException(description = "$cMethodName must be called from a Worker Thread")
    }
}
