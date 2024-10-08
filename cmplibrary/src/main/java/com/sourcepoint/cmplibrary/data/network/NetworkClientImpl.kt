package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.CustomConsentReq
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.model.toBodyRequest
import com.sourcepoint.cmplibrary.model.toBodyRequestDeleteCustomConsentTo
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.mobile_core.network.SourcepointClient
import com.sourcepoint.mobile_core.network.requests.ConsentStatusRequest
import com.sourcepoint.mobile_core.network.requests.MetaDataRequest
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
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
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<CustomConsentResp> = check {
        val mediaType = "application/json".toMediaType()
        val jsonBody = customConsentReq.toBodyRequest()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)
        val url = urlManager.sendCustomConsentUrl(env)

        logger.req(
            tag = "CustomConsentReq",
            url = url.toString(),
            body = jsonBody,
            type = "POST"
        )

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseCustomConsentRes(response)
    }

    override fun deleteCustomConsentTo(
        customConsentReq: CustomConsentReq,
        env: Env
    ): Either<CustomConsentResp> = check {
        val mediaType = "application/json".toMediaType()
        val jsonBody = customConsentReq.toBodyRequestDeleteCustomConsentTo()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)
        val url = urlManager.deleteCustomConsentToUrl(env.host, customConsentReq)

        logger.req(
            tag = "DeleteCustomConsentReq",
            url = url.toString(),
            body = jsonBody,
            type = "DELETE"
        )

        val request: Request = Request.Builder()
            .url(url)
            .delete(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseCustomConsentRes(response)
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

    override fun postPvData(param: PvDataParamReq): Either<PvDataResp> = check(ApiRequestPostfix.PV_DATA) {
        val url = urlManager.getPvDataUrl(param.env)
        val mediaType = "application/json".toMediaType()
        val jsonBody = param.body.toString()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)

        logger.req(
            tag = "PvDataRequest - ${param.campaignType.name}",
            url = url.toString(),
            body = jsonBody,
            type = "POST"
        )

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parsePvDataResp(response)
    }

    override fun getChoice(param: GetChoiceParamReq): Either<ChoiceResp> = check(ApiRequestPostfix.GET_CHOICE) {
        val url = urlManager.getChoiceUrl(param)

        logger.req(
            tag = "getChoiceUrl",
            url = url.toString(),
            body = check { JsonConverter.converter.encodeToString(param) }.getOrNull() ?: "",
            type = "GET"
        )

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseGetChoiceResp(response, param.choiceType)
    }

    override fun storeGdprChoice(param: PostChoiceParamReq): Either<GdprCS> = check(ApiRequestPostfix.POST_CHOICE_GDPR) {
        val url = urlManager.getGdprChoiceUrl(param)
        val mediaType = "application/json".toMediaType()
        val jsonBody = param.body.toString()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)

        logger.req(
            tag = "storeGdprChoice",
            url = url.toString(),
            body = jsonBody,
            type = "POST"
        )

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parsePostGdprChoiceResp(response)
    }

    override fun storeCcpaChoice(param: PostChoiceParamReq): Either<CcpaCS> = check(ApiRequestPostfix.POST_CHOICE_CCPA) {
        val url = urlManager.getCcpaChoiceUrl(param)
        val mediaType = "application/json".toMediaType()
        val jsonBody = param.body.toString()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)

        logger.req(
            tag = "storeCcpaChoice",
            url = url.toString(),
            body = jsonBody,
            type = "POST"
        )

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parsePostCcpaChoiceResp(response)
    }

    override fun storeUsNatChoice(
        param: PostChoiceParamReq,
    ): Either<USNatConsentData> = check(ApiRequestPostfix.POST_CHOICE_USNAT) {

        val url = urlManager.postUsNatChoiceUrl(param)
        val mediaType = "application/json".toMediaType()
        val jsonBody = param.body.toString()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)

        logger.req(
            tag = "storeUsNatChoice",
            url = url.toString(),
            body = jsonBody,
            type = "POST"
        )

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parsePostUsNatChoiceResp(response)
    }
}
