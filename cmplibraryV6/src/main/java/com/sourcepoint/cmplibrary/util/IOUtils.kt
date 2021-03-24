package com.sourcepoint.cmplibrary.util

import android.content.Context
import java.io.InputStreamReader

fun String.file2String(): String = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readText() }

fun Context.fileFromAssets2String(name: String) = InputStreamReader(assets.open(name)).use { it.readText() }

fun String.file2List(): List<String> = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readLines() }
