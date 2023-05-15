package com.sourcepoint.cmplibrary.util.extensions

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import org.json.JSONObject

/**
 * Extension function that transforms JSONObject from "org.json" library to JsonObject from
 * "kotlinx.serialization" library
 */
fun JSONObject.toJsonObject(): JsonObject = Json.parseToJsonElement(this.toString()).jsonObject
