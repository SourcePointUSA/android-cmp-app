package com.sourcepoint.cmplibrary.util

import android.content.Context

fun Context.file2String(resId: Int): String {
    return resources
        .openRawResource(resId)
        .bufferedReader()
        .use { it.readText() }
}

fun String.file2String(): String = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readText() }