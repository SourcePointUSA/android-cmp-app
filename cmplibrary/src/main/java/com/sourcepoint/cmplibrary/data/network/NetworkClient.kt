package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import com.sourcepoint.mobile_core.network.requests.MetaDataRequest
import com.sourcepoint.mobile_core.network.responses.ConsentStatusResponse
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse

const val DEFAULT_TIMEOUT = 10000L

/**
 * Component used to handle the network request
 */
internal interface NetworkClient {

    fun sendCustomConsent(
        consentUUID: String,
        propertyId: Int,
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>
    ): GDPRConsent

    fun deleteCustomConsentTo(
        consentUUID: String,
        propertyId: Int,
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>
    ): GDPRConsent

    fun getMetaData(campaigns: MetaDataRequest.Campaigns): MetaDataResponse

    fun getConsentStatus(authId: String?, metadata: ConsentStatusRequest.MetaData): ConsentStatusResponse

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
