package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.util.Either

internal interface JsonConverter {
    fun toUWResp(body: String): Either<UWResp>
    companion object
}
