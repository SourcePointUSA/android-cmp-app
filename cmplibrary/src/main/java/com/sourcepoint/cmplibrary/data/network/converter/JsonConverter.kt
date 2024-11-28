package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.сonsentStatus.USNatCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import kotlinx.serialization.json.JsonObject

/**
 * Component used to convert the response body of the message call to its DTO
 */
internal interface JsonConverter {
    /**
     * @param body json object
     * @return [Either] object contain either a DTO or an [Throwable]
     */

    fun toConsentAction(body: String): Either<ConsentActionImpl>

    fun toNativeMessageDto(body: String): Either<NativeMessageDto>

    fun toNativeMessageRespK(body: String): Either<NativeMessageRespK>

    fun toChoiceResp(body: String): Either<ChoiceResp>

    fun toGdprPostChoiceResp(body: String): Either<GdprCS>

    fun toCcpaPostChoiceResp(body: String): Either<CcpaCS>

    fun toUsNatPostChoiceResp(body: String): Either<USNatCS>

    fun toMessagesResp(body: String): Either<MessagesResp>

    fun toJsonObject(body: String): JsonObject

    companion object
}
