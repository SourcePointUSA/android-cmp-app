package com.sourcepoint.cmplibrary.util

import android.os.Looper

internal fun checkMainThread(cMethodName: String) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        throw Exception()
    }
}

internal fun checkWorkerThread(cMethodName: String) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        throw Exception()
    }
}
