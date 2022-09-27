package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test

class Messages2Test {

    @Test
    fun mess() {
        val mess = "v7/messagesObj.json".file2String()
        val obj = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
            prettyPrint = true
            prettyPrintIndent = "  "
            coerceInputValues = true
            useArrayPolymorphism = true
            allowSpecialFloatingPointValues = true
        }.decodeFromString<Messages2>(mess)
        println(obj)
    }
}