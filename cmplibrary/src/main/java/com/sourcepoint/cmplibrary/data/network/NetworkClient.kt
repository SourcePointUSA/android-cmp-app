package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.v7.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.v7.ConsentStatusParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.v7.MetaDataParamReq
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageRequest
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import org.json.JSONObject

/**
 * Component used to handle the network request
 */
internal interface NetworkClient {

    /**
     * Requesting a message object to the server
     * @param messageReq request content to send into the body
     * @param pSuccess success callback
     * @param pError error callback
     */
    fun getUnifiedMessage(
        messageReq: UnifiedMessageRequest,
        pSuccess: (UnifiedMessageResp) -> Unit,
        pError: (Throwable) -> Unit,
        env: Env
    )

    fun sendConsent(
        consentReq: JSONObject,
        env: Env,
        consentActionImpl: ConsentActionImpl
    ): Either<ConsentResp>

    fun sendCustomConsent(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<CustomConsentResp>

    fun deleteCustomConsentTo(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<CustomConsentResp>

    // V7
    fun getMetaData(
        param: MetaDataParamReq
    ): Either<MetaDataResp>

    fun getConsentStatus(
        param: ConsentStatusParamReq
    ): Either<ConsentStatusResp>

    fun getMessages(
        messageReq: MessagesParamReq,
        pSuccess: (MessagesResp) -> Unit,
        pError: (Throwable) -> Unit
    )

    fun getMessages(
        param: MessagesParamReq
    ): Either<MessagesResp>

    fun savePvData(
        param: PvDataParamReq
    ): Either<PvDataResp>

    fun getChoice(
        param: ChoiceParamReq
    ): Either<ChoiceResp>

    fun storeGdprChoice(
        param: PostChoiceParamReq
    ): Either<GdprPostChoiceResp>

    fun storeCcpaChoice(
        param: PostChoiceParamReq
    ): Either<CcpaPostChoiceResp>
}
