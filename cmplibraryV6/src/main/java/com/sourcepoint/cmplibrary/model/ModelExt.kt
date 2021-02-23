package com.sourcepoint.cmplibrary.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.* // ktlint-disable
import kotlin.collections.ArrayList

fun JSONObject.toTreeMap(): Map<String, Any?> {
    return when (this) {
        JSONObject.NULL -> TreeMap()
        else -> toMap(this)
    }
}

fun Map<String, Any?>.getMap(key: String): Map<String, Any?>? {
    return this[key] as? Map<String, Any?>
}

fun Map<String, Any?>.getList(key: String): List<Map<String, Any?>>? {
    return this[key] as? List<Map<String, Any?>>
}

fun <T> Map<String, Any?>.getFieldValue(key: String): T? {
    return this[key] as? T
}

private fun toMap(jsonObj: JSONObject): Map<String, Any?> {
    val map: MutableMap<String, Any?> = TreeMap()
    val keysItr = jsonObj.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
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
