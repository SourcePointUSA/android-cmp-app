package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.Either

internal interface JsonConverter {
    fun toUWResp() : Either<UWResp>
    companion object
}