package com.sourcepoint.gdpr_cmplibrary.data.network

import com.sourcepoint.gdpr_cmplibrary.data.network.model.NativeMessageReq
import org.json.JSONObject

interface NetworkClient {
    fun getNativeMessage(
        nativeMessageReq: NativeMessageReq,
        success: (JSONObject) -> Unit,
        error: (Throwable) -> Unit
    )
}