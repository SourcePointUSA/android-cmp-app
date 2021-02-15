package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.layout.json.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageRespK
import com.sourcepoint.cmplibrary.data.network.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.util.Either

/**
 * Component used to convert the response body of the message call to its DTO
 */
internal interface JsonConverter {
    /**
     * @param body json object
     * @return [Either] object contain either a DTO or an [Throwable]
     */

    fun toUnifiedMessageResp(body: String): Either<UnifiedMessageResp>

    fun toNativeMessageResp(body: String): Either<NativeMessageResp>
    fun toNativeMessageRespK(body: String): Either<NativeMessageRespK>

    fun toNativeMessageDto(body: String): Either<NativeMessageDto>

    fun toConsentAction(json: String): Either<ConsentAction>
    companion object
}
