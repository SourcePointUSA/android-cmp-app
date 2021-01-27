package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.Either

/**
 * Factory method for building an instance of JsonConverter
 */
internal fun JsonConverter.Companion.create(): JsonConverter = JsonConverterImpl()


private class JsonConverterImpl : JsonConverter {
    override fun toUWResp(): Either<UWResp> {
        TODO("Not yet implemented")
    }
}