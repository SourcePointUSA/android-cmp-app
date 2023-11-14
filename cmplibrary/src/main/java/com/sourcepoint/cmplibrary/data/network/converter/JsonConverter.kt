package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp

/**
 * Component used to convert the response body of the message call to its DTO
 */
internal interface JsonConverter {
    /**
     * @param body json object
     * @return [Either] object contain either a DTO or an [Throwable]
     */

    fun toConsentResp(body: String, campaignType: CampaignType): Either<ConsentResp>

    fun toConsentAction(body: String): Either<ConsentActionImpl>

    fun toCustomConsentResp(body: String): Either<CustomConsentResp>

    fun toNativeMessageDto(body: String): Either<NativeMessageDto>

    fun toNativeMessageRespK(body: String): Either<NativeMessageRespK>

    // Optimized
    fun toMetaDataRespResp(body: String): Either<MetaDataResp>

    fun toConsentStatusResp(body: String): Either<ConsentStatusResp>

    fun toChoiceResp(body: String): Either<ChoiceResp>

    fun toGdprPostChoiceResp(body: String): Either<GdprCS>

    fun toCcpaPostChoiceResp(body: String): Either<CcpaCS>

    fun toUsNatPostChoiceResp(body: String): Either<USNatConsentData>

    fun toPvDataResp(body: String): Either<PvDataResp>

    fun toMessagesResp(body: String): Either<MessagesResp>

    companion object
}
