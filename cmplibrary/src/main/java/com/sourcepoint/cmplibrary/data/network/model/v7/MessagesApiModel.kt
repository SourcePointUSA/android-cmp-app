package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import okhttp3.HttpUrl
import org.json.JSONObject

internal data class MessagesParamReq(
    val env: Env,
    val metadata: String,
    val body: String,
    val nonKeyedLocalState: String
)
