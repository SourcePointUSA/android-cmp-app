package com.sourcepoint.cmplibrary.util.extensions

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

fun JsonObject.putAdditionalData(dataMap: Map<String, Any>): JsonObject {
    return this.toMutableMap().apply {
        dataMap.forEach {
            when (it.value) {
                is String -> put(it.key, JsonPrimitive(it.value as String))
                is Number -> put(it.key, JsonPrimitive(it.value as Number))
                is Boolean -> put(it.key, JsonPrimitive(it.value as Boolean))
                else -> throw IllegalArgumentException("Unsupported type: ${it.value.javaClass}")
            }
        }
    }.let { JsonObject(it) }
}
