package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.util.Either
import okhttp3.Response

/**
 * Component used to parse an OkHttp [Response] and extract a DTO
 */
internal interface ResponseManager {
    /**
     * Parsing a [MessageResp]
     * @param r http response
     * @return [Either] object
     */
    fun parseResponse(r: Response): Either<MessageResp>
    companion object
}
