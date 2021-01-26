package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.Either
import okhttp3.Response
import org.json.JSONObject

internal interface ResponseManager {
    fun parseResponse(r: Response) : Either<UWResp>
}