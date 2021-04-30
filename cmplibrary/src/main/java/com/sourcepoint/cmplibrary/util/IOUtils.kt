package com.sourcepoint.cmplibrary.util

internal fun String.file2String(): String = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readText() }

internal fun String.file2List(): List<String> = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readLines() }
