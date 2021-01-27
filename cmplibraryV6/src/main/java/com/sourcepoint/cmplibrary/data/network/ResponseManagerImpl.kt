package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import okhttp3.Response

internal fun createResponseManager() : ResponseManager = ResponseManagerImpl()

private class ResponseManagerImpl : ResponseManager {
    override fun parseResponse(r: Response): Either<UWResp> = check {
        UWResp(null)
    }
}