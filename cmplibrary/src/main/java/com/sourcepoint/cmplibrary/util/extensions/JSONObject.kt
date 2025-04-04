package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.mobile_core.network.json
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject

fun JSONObject.toJsonObject() = json.parseToJsonElement(toString()).jsonObject
