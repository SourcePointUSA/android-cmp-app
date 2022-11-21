package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import kotlinx.serialization.json.JsonObject

internal class PostChoiceParamReq(
    val env: Env,
    val actionType: ActionType,
    val body: JsonObject = JsonObject(mapOf())
)
