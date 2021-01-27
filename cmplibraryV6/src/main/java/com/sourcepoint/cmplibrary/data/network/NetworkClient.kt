package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.util.Either

internal interface NetworkClient {
    fun getMessage(
        uwReq: UWReq,
        success: (UWResp) -> Unit,
        error: (Throwable) -> Unit)

    suspend fun getMessage(uwReq: UWReq) : Either<UWResp>
}