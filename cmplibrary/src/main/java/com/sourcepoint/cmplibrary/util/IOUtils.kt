package com.sourcepoint.cmplibrary.util

import android.content.Context

internal fun String.file2String(): String = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readText() }

internal fun String.file2List(): List<String> = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readLines() }

internal fun Context.readFromAsset(fileName: String): String {
    return assets
        .open(fileName)
        .reader()
        .readText()
}
