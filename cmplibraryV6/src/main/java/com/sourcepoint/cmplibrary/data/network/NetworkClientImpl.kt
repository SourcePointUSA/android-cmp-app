package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.util.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ConsentResp
import com.sourcepoint.cmplibrary.model.MessageReq
import com.sourcepoint.cmplibrary.model.UnifiedMessageRequest
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.ext.toBodyRequest
import com.sourcepoint.cmplibrary.util.check
import okhttp3.* // ktlint-disable
import org.json.JSONObject

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
    private val responseManager: ResponseManager = ResponseManager.create(JsonConverter.create()),
) : NetworkClient {

    override fun getUnifiedMessage(
        messageReq: UnifiedMessageRequest,
        pSuccess: (UnifiedMessageResp) -> Unit,
        pError: (Throwable) -> Unit,
        env: Env
    ) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, messageReq.toBodyRequest())
        val url = urlManager.inAppMessageUrl(env).also { logger.i(NetworkClientImpl::class.java.name, "url getUnifiedMessage [$it]") }

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        httpClient
            .newCall(request)
            .enqueue {
                onFailure { _, exception ->
                    pError(exception)
                }
                onResponse { _, r ->
                    responseManager
                        .parseResponse1203(r)
                        .map { pSuccess(it) }
                        .executeOnLeft { pError(it) }
                }
            }
    }

    // TODO verify if we need it
    override fun getNativeMessage(
        messageReq: MessageReq,
        success: (NativeMessageResp) -> Unit,
        error: (Throwable) -> Unit
    ) {

        val bodyContent = """
            {
                "accountId": 22,
                "propertyHref": "https://tcfv2.mobile.demo",
                "requestUUID": "test",
                "meta": "{}",
                "alwaysDisplayDNS": false
              }
        """.trimIndent()

        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, bodyContent)

        val request: Request = Request.Builder()
//            .url(urlManager.inAppUrlNativeMessage)
            .post(body)
            .build()

        httpClient
            .newCall(request)
            .enqueue {
                onFailure { _, exception ->
                    error(exception)
                }
                onResponse { _, r ->
                    responseManager
                        .parseNativeMessRes(r)
                        .map { success(it) }
                        .executeOnLeft { error(it) }
                }
            }
    }

    override fun getNativeMessageK(
        messageReq: MessageReq,
        success: (NativeMessageRespK) -> Unit,
        error: (Throwable) -> Unit
    ) {
        // TODO adapt unified wrapper logic
        val bodyContent = """
            {
                "accountId": 22,
                "propertyHref": "https://tcfv2.mobile.demo",
                "requestUUID": "test",
                "meta": "{}",
                "alwaysDisplayDNS": false
              }
        """.trimIndent()

        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, bodyContent)

        val request: Request = Request.Builder()
//            .url(urlManager.inAppUrlNativeMessage)
            .post(body)
            .build()

        httpClient
            .newCall(request)
            .enqueue {
                onFailure { _, exception ->
                    error(exception)
                }
                onResponse { _, r ->
                    responseManager
                        .parseNativeMessResK(r)
                        .map { success(it) }
                        .executeOnLeft { error(it) }
                }
            }
    }

    override fun sendConsent(
        consentReq: JSONObject,
        env: Env,
        consentAction: ConsentAction
    ): Either<ConsentResp> = check {

        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, consentReq.toString())
        val url = urlManager
            .sendConsentUrl(legislation = consentAction.legislation, env = env, actionType = consentAction.actionType)
            .also { logger.i(NetworkClientImpl::class.java.name, "url getUnifiedMessage [$it]") }

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()

        responseManager.parseConsentRes(response, consentAction.legislation)
    }
}
