package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.UWResp
import com.sourcepoint.cmplibrary.util.Either
import okhttp3.Response

/**
 * Component used to parse an OkHttp [Response] and extract a DTO
 */
internal interface ResponseManager {
    /**
     * Parsing a [UWResp]
     * @param r http response
     * @return [Either] object
     */
    fun parseResponse(r: Response): Either<UWResp>
    companion object
}
