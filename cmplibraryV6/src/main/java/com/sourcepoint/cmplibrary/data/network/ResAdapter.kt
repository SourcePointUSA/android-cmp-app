package com.sourcepoint.cmplibrary.data.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.json.JSONObject

class ResAdapter {

    @ToJson
    fun toJson(obj : UWResp): String {
        return obj.toString()
    }

    @FromJson
    fun fromJson(message: String) : UWResp {
        return UWResp(Gdpr("","", "", null))
    }
}