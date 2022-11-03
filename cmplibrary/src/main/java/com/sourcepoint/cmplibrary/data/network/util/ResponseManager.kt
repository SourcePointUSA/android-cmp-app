package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.v7.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import okhttp3.Response

/**
 * Component used to parse an OkHttp [Response] and extract a DTO
 */
internal interface ResponseManager {

    /**
     * Parsing a [UnifiedMessageResp]
     * @param r http response
     * @return [Either] object
     */
    fun parseResponse(r: Response): Either<UnifiedMessageResp>

    /**
     * @param r http response
     * @return [Either] object
     */
    fun parseNativeMessRes(r: Response): Either<NativeMessageResp>

    fun parseNativeMessResK(r: Response): Either<NativeMessageRespK>

    fun parseConsentResEither(r: Response, campaignType: CampaignType): Either<ConsentResp>
    fun parseConsentRes(r: Response, campaignType: CampaignType): ConsentResp
    fun parseCustomConsentRes(r: Response): CustomConsentResp

    // V7
    fun parseMetaDataRes(r: Response): MetaDataResp
    fun parseConsentStatusResp(r: Response): ConsentStatusResp
    fun parseGetChoiceResp(r: Response): ChoiceResp
    fun parsePostGdprChoiceResp(r: Response): GdprCS
    fun parsePostCcpaChoiceResp(r: Response): CcpaCS
    fun parsePvDataResp(r: Response): PvDataResp
    fun parseMessagesResp(r: Response): MessagesResp
    fun parseMessagesResp2(r: Response): Either<MessagesResp>

    companion object
}
