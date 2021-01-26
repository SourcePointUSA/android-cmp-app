package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.Either
import com.sourcepoint.cmplibrary.data.check
import okhttp3.Response
import org.json.JSONObject

internal fun createResponseManager() : ResponseManager = ResponseManagerImpl()

private class ResponseManagerImpl : ResponseManager {
    override fun parseResponse(r: Response): Either<UWResp> = check {
        UWResp(null)
    }
}