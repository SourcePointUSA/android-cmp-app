package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
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

internal data class PvDataParamReq(
    val env: Env,
    val body: JSONObject
)
