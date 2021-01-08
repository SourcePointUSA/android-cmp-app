package com.sourcepoint.gdpr_cmplibrary.data.network

import com.sourcepoint.gdpr_cmplibrary.data.Either
import okhttp3.Response
import org.json.JSONObject

internal interface ResponseManager {
    fun parseResponse(r: Response) : Either<JSONObject>
}