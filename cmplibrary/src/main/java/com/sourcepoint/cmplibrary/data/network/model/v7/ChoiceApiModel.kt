package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

internal class ChoiceResp(
    val thisContent: JSONObject,
)

internal class ChoiceParamReq(
    val env: Env,
    val body: JsonObject
)
