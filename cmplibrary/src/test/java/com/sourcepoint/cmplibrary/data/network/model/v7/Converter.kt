package com.sourcepoint.cmplibrary.data.network.model.v7

import kotlinx.serialization.json.Json

val converter by lazy {
    Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
        allowStructuredMapKeys = true
        explicitNulls = false
        prettyPrint = true
        prettyPrintIndent = "  "
        coerceInputValues = true
        useArrayPolymorphism = true
        allowSpecialFloatingPointValues = true
    }
}