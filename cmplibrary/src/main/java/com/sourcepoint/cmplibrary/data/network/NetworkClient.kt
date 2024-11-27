package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.mobile_core.models.SPActionType
import com.sourcepoint.mobile_core.models.SPIDFAStatus
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import com.sourcepoint.mobile_core.network.requests.CCPAChoiceRequest
import com.sourcepoint.mobile_core.network.requests.ChoiceAllMetaDataRequest
import com.sourcepoint.mobile_core.network.requests.ConsentStatusRequest
import com.sourcepoint.mobile_core.network.requests.GDPRChoiceRequest
import com.sourcepoint.mobile_core.network.requests.IncludeData
import com.sourcepoint.mobile_core.network.requests.MetaDataRequest
import com.sourcepoint.mobile_core.network.requests.PvDataRequest
import com.sourcepoint.mobile_core.network.requests.USNatChoiceRequest
import com.sourcepoint.mobile_core.network.responses.CCPAChoiceResponse
import com.sourcepoint.mobile_core.network.responses.ChoiceAllResponse
import com.sourcepoint.mobile_core.network.responses.ConsentStatusResponse
import com.sourcepoint.mobile_core.network.responses.GDPRChoiceResponse
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import com.sourcepoint.mobile_core.network.responses.PvDataResponse
import com.sourcepoint.mobile_core.network.responses.USNatChoiceResponse

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
        request: PvDataRequest
    ): PvDataResponse

    fun getChoice(
        actionType: SPActionType,
        accountId: Int,
        propertyId: Int,
        idfaStatus: SPIDFAStatus,
        metadata: ChoiceAllMetaDataRequest,
        includeData: IncludeData
    ): ChoiceAllResponse

    fun storeGdprChoice(
        actionType: SPActionType,
        request: GDPRChoiceRequest
    ): GDPRChoiceResponse

    fun storeCcpaChoice(
        actionType: SPActionType,
        request: CCPAChoiceRequest
    ): CCPAChoiceResponse

    fun storeUsNatChoice(
        actionType: SPActionType,
        request: USNatChoiceRequest
    ): USNatChoiceResponse
}
