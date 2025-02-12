package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.mobile_core.models.SPActionType
import com.sourcepoint.mobile_core.models.consents.GDPRConsent
import com.sourcepoint.mobile_core.network.SourcepointClient
import com.sourcepoint.mobile_core.network.requests.CCPAChoiceRequest
import com.sourcepoint.mobile_core.network.requests.ChoiceAllRequest
import com.sourcepoint.mobile_core.network.requests.ConsentStatusRequest
import com.sourcepoint.mobile_core.network.requests.GDPRChoiceRequest
import com.sourcepoint.mobile_core.network.requests.MetaDataRequest
import com.sourcepoint.mobile_core.network.requests.PvDataRequest
import com.sourcepoint.mobile_core.network.requests.USNatChoiceRequest
import com.sourcepoint.mobile_core.network.responses.CCPAChoiceResponse
import com.sourcepoint.mobile_core.network.responses.ChoiceAllResponse
import com.sourcepoint.mobile_core.network.responses.GDPRChoiceResponse
import com.sourcepoint.mobile_core.network.responses.PvDataResponse
import com.sourcepoint.mobile_core.network.responses.USNatChoiceResponse
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

internal fun createNetworkClient(
    accountId: Int,
    propertyId: Int,
    propertyName: String,
    httpClient: OkHttpClient,
    urlManager: HttpUrlManager,
    logger: Logger,
    responseManager: ResponseManager
): NetworkClient = NetworkClientImpl(
    httpClient,
    urlManager,
    logger,
    responseManager,
    coreClient = SourcepointClient(
        accountId = accountId,
        propertyId = propertyId,
        propertyName = propertyName
    )
)

private class NetworkClientImpl(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    private val logger: Logger,
    private val responseManager: ResponseManager = ResponseManager.create(JsonConverter.create(), logger),
    private val coreClient: SourcepointClient
) : NetworkClient {

    override fun sendCustomConsent(
        consentUUID: String,
        propertyId: Int,
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>
    ): GDPRConsent = runBlocking {
        coreClient.customConsentGDPR(
            consentUUID = consentUUID,
            propertyId = propertyId,
            vendors = vendors,
            categories = categories,
            legIntCategories = legIntCategories
        )
    }

    override fun deleteCustomConsentTo(
        consentUUID: String,
        propertyId: Int,
        vendors: List<String>,
        categories: List<String>,
        legIntCategories: List<String>
    ): GDPRConsent = runBlocking {
        coreClient.deleteCustomConsentGDPR(
            consentUUID = consentUUID,
            propertyId = propertyId,
            vendors = vendors,
            categories = categories,
            legIntCategories = legIntCategories
        )
    }

    override fun getMetaData(campaigns: MetaDataRequest.Campaigns) = runBlocking {
        coreClient.getMetaData(campaigns)
    }

    override fun getConsentStatus(authId: String?, metadata: ConsentStatusRequest.MetaData) = runBlocking {
        coreClient.getConsentStatus(authId = authId, metadata = metadata)
    }

    override fun getMessages(param: MessagesParamReq): Either<MessagesResp> = check(ApiRequestPostfix.MESSAGES) {
        val url = urlManager.getMessagesUrl(param)

        logger.req(
            tag = "getMessages",
            url = url.toString(),
            body = param.body,
            type = "GET"
        )

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseMessagesResp(response)
    }

    override fun postPvData(request: PvDataRequest): PvDataResponse = runBlocking {
        coreClient.postPvData(request)
    }

    override fun getChoice(
        actionType: SPActionType,
        campaigns: ChoiceAllRequest.ChoiceAllCampaigns
    ): ChoiceAllResponse = runBlocking {
        coreClient.getChoiceAll(
            actionType = actionType,
            campaigns = campaigns
        )
    }

    override fun storeGdprChoice(actionType: SPActionType,request: GDPRChoiceRequest): GDPRChoiceResponse = runBlocking {
        coreClient.postChoiceGDPRAction(actionType = actionType, request = request)
    }

    override fun storeCcpaChoice(actionType: SPActionType,request: CCPAChoiceRequest): CCPAChoiceResponse = runBlocking {
        coreClient.postChoiceCCPAAction(actionType = actionType, request = request)
    }

    override fun storeUsNatChoice(actionType: SPActionType,request: USNatChoiceRequest): USNatChoiceResponse = runBlocking {
        coreClient.postChoiceUSNatAction(actionType = actionType, request = request)
    }
}
