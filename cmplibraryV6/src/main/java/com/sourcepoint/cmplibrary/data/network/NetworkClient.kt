package com.sourcepoint.cmplibrary.data.network

interface NetworkClient {
    fun getNativeMessage(
        uwReq: UWReq,
        success: (UWResp) -> Unit,
        error: (Throwable) -> Unit)
}