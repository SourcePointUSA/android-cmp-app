package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.util.Either
import okhttp3.Response

internal interface ResponseManager {
    fun parseResponse(r: Response): Either<UWResp>
    companion object
}
