package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatusResp
import com.sourcepoint.cmplibrary.data.network.model.v7.MetaDataResp
import com.sourcepoint.cmplibrary.data.network.model.v7.PvDataResp
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp

/**
 * Component used to convert the response body of the message call to its DTO
 */
internal interface JsonConverter {
    /**
     * @param body json object
     * @return [Either] object contain either a DTO or an [Throwable]
     */

    fun toUnifiedMessageResp(body: String): Either<UnifiedMessageResp>

    fun toConsentResp(body: String, campaignType: CampaignType): Either<ConsentResp>

    fun toConsentAction(body: String): Either<ConsentActionImpl>

    fun toCustomConsentResp(body: String): Either<CustomConsentResp>

    fun toNativeMessageDto(body: String): Either<NativeMessageDto>

    fun toNativeMessageResp(body: String): Either<NativeMessageResp>

    fun toNativeMessageRespK(body: String): Either<NativeMessageRespK>

    // V7
    fun toMetaDataRespResp(body: String): Either<MetaDataResp>

    fun toConsentStatusResp(body: String): Either<ConsentStatusResp>

    fun toPvDataResp(body: String): Either<PvDataResp>

    companion object
}
