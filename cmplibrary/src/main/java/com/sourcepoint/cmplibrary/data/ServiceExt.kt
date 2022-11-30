package com.sourcepoint.cmplibrary.data

import org.json.JSONObject

fun JSONObject.getStringOrNullByKey(
    key: String
): String?{
    return getString(key)?:null
}

fun JSONObject.getJSONObjectOrNullByKey(
    key: String
): JSONObject?{
    return getJSONObject(key)?:null
}
