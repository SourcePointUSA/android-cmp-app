package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import org.json.JSONArray
import org.json.JSONObject
import java.util.* // ktlint-disable
import kotlin.collections.ArrayList

internal fun String.jsonToTreeMap(): Map<String, Any?> {
    return when (this) {
        JSONObject.NULL -> TreeMap()
        else -> toMap(JSONObject(this))
    }
}

internal fun JSONObject.toTreeMap(): Map<String, Any?> {
    return when (this) {
        JSONObject.NULL -> TreeMap()
        else -> toMap(this)
    }
}

internal fun Map<String, Any?>.getMap(key: String): Map<String, Any?>? {
    return this[key] as? Map<String, Any?>
}

internal fun Map<String, Any?>.getList(key: String): List<Map<String, Any?>>? {
    return this[key] as? List<Map<String, Any?>>
}

internal fun Map<String, Any?>.toJSONObj(): JSONObject {
    return JSONObject(this)
}

internal fun Map<String, GDPRPurposeGrants?>.toJSONObjGrant(): JSONObject {
    return JSONObject().also { jo ->
        this.forEach {
            jo.put(
                it.key,
                JSONObject().apply {
                    put("granted", it.value?.granted)
                    put("purposeGrants", JSONObject(it.value?.purposeGrants))
                }
            )
        }
    }
}

internal fun <T> Map<String, Any?>.getFieldValue(key: String): T? {
    return this[key] as? T
}

private fun toMap(jsonObj: JSONObject): Map<String, Any?> {
    val map: MutableMap<String, Any?> = TreeMap()
    val keysItr = jsonObj.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
        if (jsonObj.isNull(key)) continue
        var value = jsonObj[key]
        when (value) {
            is JSONArray -> value = toList(value)
            is JSONObject -> value = toMap(value)
        }
        map[key] = value
    }
    return map
}

private fun toList(array: JSONArray): List<Any> {
    val list: MutableList<Any> = ArrayList()
    for (i in 0 until array.length()) {
        var value = array[i]
        when (value) {
            is JSONArray -> value = toList(value)
            is JSONObject -> value = toMap(value)
        }
        list.add(value)
    }
    return list
}
