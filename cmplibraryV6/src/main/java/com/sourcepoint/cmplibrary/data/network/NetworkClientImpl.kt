package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.data.network.util.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.util.executeOnLeft
import com.sourcepoint.cmplibrary.util.map
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

internal fun createNetworkClient(
    httpClient: OkHttpClient,
    urlManager: HttpUrlManager,
    responseManager: ResponseManager
): NetworkClient = NetworkClientImpl(httpClient, urlManager, responseManager)

private class NetworkClientImpl(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val urlManager: HttpUrlManager = HttpUrlManagerSingleton,
    private val responseManager: ResponseManager = ResponseManager.create(JsonConverter.create())
) : NetworkClient {

    override fun getMessage1203(
        messageReq: MessageReq,
        pSuccess: (UnifiedMessageResp1203) -> Unit,
        pError: (Throwable) -> Unit
    ) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, messageReq.toBodyRequest())

        val request: Request = Request.Builder()
            .url(urlManager.inAppUrlMessage1203)
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

    override fun getMessage(
        messageReq: MessageReq,
        pSuccess: (UnifiedMessageResp) -> Unit,
        pError: (Throwable) -> Unit
    ) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, messageReq.toBodyRequest())

        val request: Request = Request.Builder()
            .url(urlManager.inAppUrlMessage)
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
                        .parseResponse(r)
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
                "propertyId": 7094,
                "propertyHref": "https://tcfv2.mobile.demo",
                "requestUUID": "test",
                "meta": "{}",
                "alwaysDisplayDNS": false
              }
        """.trimIndent()

        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, bodyContent)

        val request: Request = Request.Builder()
            .url(urlManager.inAppUrlNativeMessage)
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
                "propertyId": 7094,
                "propertyHref": "https://tcfv2.mobile.demo",
                "requestUUID": "test",
                "meta": "{}",
                "alwaysDisplayDNS": false
              }
        """.trimIndent()

        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, bodyContent)

        val request: Request = Request.Builder()
            .url(urlManager.inAppUrlNativeMessage)
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
        legislation: Legislation,
        consentReq: JSONObject,
        success: (ConsentResp) -> Unit,
        error: (Throwable) -> Unit
    ) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, consentReq.toString())

        val request: Request = Request.Builder()
            .url(urlManager.sendConsentUrl(legislation))
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
                        .parseConsentRes(r)
                        .map { success(it) }
                        .executeOnLeft { error(it) }
                }
            }
    }
}
