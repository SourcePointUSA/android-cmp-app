package com.sourcepoint.cmplibrary.data.network.converted

import com.sourcepoint.cmplibrary.data.network.model.UWResp
import com.sourcepoint.cmplibrary.util.Either

/**
 * Component used to convert the response body of the message call to its DTO
 */
internal interface JsonConverter {
    /**
     * @param body json object
     * @return [Either] object contain either a DTO or an [Throwable]
     */
    fun toUWResp(body: String): Either<UWResp>
    companion object
}
