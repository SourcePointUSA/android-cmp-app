package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatusParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.* // ktlint-disable

const val DEFAULT_TIMEOUT = 10000L

/**
 * Component used to handle the network request
 */
internal interface NetworkClient {

    fun sendCustomConsent(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<CustomConsentResp>

    fun deleteCustomConsentTo(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<CustomConsentResp>

    // Optimized
    fun getMetaData(
        param: MetaDataParamReq
    ): Either<MetaDataResp>

    fun getConsentStatus(
        param: ConsentStatusParamReq
    ): Either<ConsentStatusResp>

    fun getMessages(
        param: MessagesParamReq
    ): Either<MessagesResp>

    fun postPvData(
        param: PvDataParamReq
    ): Either<PvDataResp>

    fun getChoice(
        param: GetChoiceParamReq
    ): Either<ChoiceResp>

    fun storeGdprChoice(
        param: PostChoiceParamReq
    ): Either<GdprCS>

    fun storeCcpaChoice(
        param: PostChoiceParamReq
    ): Either<CcpaCS>

    fun storeUsNatChoice(
        param: PostChoiceParamReq,
    ): Either<USNatConsentData>
}
