package com.sourcepoint.cmplibrary.data.network

import android.accounts.NetworkErrorException
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.gdpr_cmplibrary.exception.InvalidResponseWebMessageException
import okhttp3.Response

internal fun ResponseManager.Companion.create(
    JsonConverter: JsonConverter
): ResponseManager = ResponseManagerImpl(JsonConverter)

private class ResponseManagerImpl(val jsonConverter: JsonConverter) : ResponseManager {
    override fun parseResponse(r: Response): Either<UWResp> = check {
        if (r.isSuccessful) {
            val body = r.body()?.byteStream()?.reader()?.readText() ?: fail("Body Response")
            when (val either: Either<UWResp> = jsonConverter.toUWResp(body)) {
                is Either.Right -> either.r
                is Either.Left -> throw either.t
            }
        } else {
            throw NetworkErrorException("$r")
        }
    }

    private fun fail(param: String): Nothing {
        throw InvalidResponseWebMessageException(description = "$param object is null")
    }
}
