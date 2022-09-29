package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

object GrantsSerializer : JsonTransformingSerializer<Map<String, GDPRPurposeGrants>>(
    tSerializer = MapSerializer(String.serializer(), GDPRPurposeGrants.serializer())
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
