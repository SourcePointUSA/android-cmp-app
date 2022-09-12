package com.sourcepoint.cmplibrary.data.network.model.v7

import org.json.JSONObject

internal data class PvDataResp(
    val thisContent: JSONObject,
    val gdprPv: GdprPv?
)

internal data class GdprPv(
    val thisContent: JSONObject,
    val uuid: String,
    val cookies: List<JSONObject>
)
