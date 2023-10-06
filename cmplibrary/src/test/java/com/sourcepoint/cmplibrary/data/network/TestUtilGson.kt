package com.sourcepoint.cmplibrary.data.network

interface TestUtilGson {

    companion object {
        /**
         * Receive file.json and return the content as string
         */
        fun String.jsonFile2String(): String = Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream(this)
            .bufferedReader().use { it.readText() }
    }
}
