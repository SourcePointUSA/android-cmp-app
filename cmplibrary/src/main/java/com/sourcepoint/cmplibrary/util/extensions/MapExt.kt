package com.sourcepoint.cmplibrary.util.extensions

import kotlinx.serialization.json.JsonElement

internal fun Map<String, JsonElement>?.toMapOfAny(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    this?.forEach { (t, u) -> map[t] = u }
    return map
}
