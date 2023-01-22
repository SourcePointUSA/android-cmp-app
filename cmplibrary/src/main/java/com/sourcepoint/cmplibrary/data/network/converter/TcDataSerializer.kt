package com.sourcepoint.cmplibrary.data.network.converter

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

object TcDataSerializer : JsonTransformingSerializer<Map<String, JsonElement>>(
    tSerializer = MapSerializer(String.serializer(), JsonElement.serializer())
) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val map =
            (element as? Map<String, JsonElement>)?.entries?.fold(mutableMapOf<String, JsonElement>()) { acc, entry ->
                acc[entry.key] = entry.value
                acc
            } ?: emptyMap()
        return JsonObject(map)
    }
}

internal fun Map<String, JsonElement>?.toMapOfAny(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    this?.forEach { (t, u) -> map[t] = u }
    return map
}
