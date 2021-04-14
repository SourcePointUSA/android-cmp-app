package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ConsentResp

/**
 * Component used to convert the response body of the message call to its DTO
 */
internal interface JsonConverter {
    /**
     * @param body json object
     * @return [Either] object contain either a DTO or an [Throwable]
     */

    fun toUnifiedMessageResp1203(body: String): Either<UnifiedMessageResp1203>

    fun toNativeMessageResp(body: String): Either<NativeMessageResp>

    fun toNativeMessageRespK(body: String): Either<NativeMessageRespK>

    fun toConsentResp(body: String, legislation: Legislation): Either<ConsentResp>

    fun toNativeMessageDto(body: String): Either<NativeMessageDto>

    fun toConsentAction(body: String): Either<ConsentAction>

    companion object
}
