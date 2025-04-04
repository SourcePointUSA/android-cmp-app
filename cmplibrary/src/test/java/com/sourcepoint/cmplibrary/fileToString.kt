package com.sourcepoint.cmplibrary

fun String.file2String(): String = Thread.currentThread()
    .contextClassLoader
    .getResourceAsStream(this)
    .bufferedReader().use { it.readText() }
