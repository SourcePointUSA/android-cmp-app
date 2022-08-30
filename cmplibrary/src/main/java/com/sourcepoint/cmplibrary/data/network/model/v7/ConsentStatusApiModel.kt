package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import org.json.JSONObject

internal class ConsentStatusResp(
    val thisContent: JSONObject,
    val localState: JSONObject
)

internal data class ConsentStatusParamReq(
    val env: Env,
    val metadata: String,
    val propertyId: Int,
    val hasCsp: Boolean,
    val withSiteActions: Boolean,
    val accountId: Int,
    val authId: String? = null
)
