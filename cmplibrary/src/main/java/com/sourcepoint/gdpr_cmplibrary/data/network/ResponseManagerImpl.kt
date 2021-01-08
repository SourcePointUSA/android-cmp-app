package com.sourcepoint.gdpr_cmplibrary.data.network

import com.sourcepoint.gdpr_cmplibrary.data.Either
import com.sourcepoint.gdpr_cmplibrary.data.check
import okhttp3.Response
import org.json.JSONObject

internal fun createResponseManager() : ResponseManager = ResponseManagerImpl()

private class ResponseManagerImpl : ResponseManager {
    override fun parseResponse(r: Response): Either<JSONObject> = check {
        JSONObject(r.body()!!.string())
    }
}