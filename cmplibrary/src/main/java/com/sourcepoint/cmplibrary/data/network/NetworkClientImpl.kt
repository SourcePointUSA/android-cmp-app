package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.util.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.exception.ApiRequestSuffix
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import kotlinx.serialization.encodeToString
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

internal fun createNetworkClient(
    httpClient: OkHttpClient,
    urlManager: HttpUrlManager,
    logger: Logger,
    responseManager: ResponseManager
): NetworkClient = NetworkClientImpl(httpClient, urlManager, logger, responseManager)

private class NetworkClientImpl(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    private val logger: Logger,
    private val responseManager: ResponseManager = ResponseManager.create(JsonConverter.create(), logger),
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

    override fun getMetaData(param: MetaDataParamReq): Either<MetaDataResp> = check(ApiRequestSuffix.META_DATA) {
        val url = urlManager.getMetaDataUrl(param)

        logger.req(
            tag = "getMetaData",
            url = url.toString(),
            body = "",
            type = "GET"
        )

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseMetaDataRes(response)
    }

    override fun getConsentStatus(param: ConsentStatusParamReq): Either<ConsentStatusResp> = check(ApiRequestSuffix.CONSENT_STATUS) {
        val url = urlManager.getConsentStatusUrl(param)

        logger.req(
            tag = "getConsentStatus",
            url = url.toString(),
            body = check { JsonConverter.converter.encodeToString(param) }.getOrNull() ?: "",
            type = "GET"
        )

        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseConsentStatusResp(response)
    }

    override fun getMessages(param: MessagesParamReq): Either<MessagesResp> = check(ApiRequestSuffix.MESSAGES) {
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

    override fun postPvData(param: PvDataParamReq): Either<PvDataResp> = check(ApiRequestSuffix.PV_DATA) {
        val url = urlManager.getPvDataUrl(param.env)
        val mediaType = "application/json".toMediaType()
        val jsonBody = param.body.toString()
        val body: RequestBody = RequestBody.create(mediaType, jsonBody)

        logger.req(
            tag = "savePvData - ${param.campaignType.name}",
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

    override fun getChoice(param: GetChoiceParamReq): Either<ChoiceResp> = check(ApiRequestSuffix.GET_CHOICE) {
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

//        val response = httpClient.newCall(request).execute()
        val response = Response.Builder()
            .code(444)
            .protocol(Protocol.HTTP_1_1)
            .request(Request.Builder().url(url).build())
            .message("")
            .body(ResponseBody.create("application/json".toMediaTypeOrNull(), ""))
            .build()

        responseManager.parseGetChoiceResp(response)
    }

    override fun storeGdprChoice(param: PostChoiceParamReq): Either<GdprCS> = check(ApiRequestSuffix.POST_CHOICE_GDPR) {
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

    override fun storeCcpaChoice(param: PostChoiceParamReq): Either<CcpaCS> = check(ApiRequestSuffix.POST_CHOICE_CCPA) {
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
}
