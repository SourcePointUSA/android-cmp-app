package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.MessageReq
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.data.network.model.toBodyRequest
import com.sourcepoint.cmplibrary.data.network.util.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.util.executeOnLeft
import com.sourcepoint.cmplibrary.util.map
import okhttp3.* // ktlint-disable

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

    override fun getMessage(
        messageReq: MessageReq,
        pSuccess: (MessageResp) -> Unit,
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

    override fun getNativeMessage(
        messageReq: MessageReq,
        success: (NativeMessageResp) -> Unit,
        error: (Throwable) -> Unit
    ) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, bodyString)

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

    val bodyString = """
        {
          "accountId": 22,
          "euconsent": "",
          "propertyId": 7094,
          "requestUUID": "edad7293-2f81-4eb8-9960-333acd9bc738",
          "uuid": "b1cc71f9-2e2c-465b-96d4-db039f6d0fb3",
          "meta": "{\"mmsCookies\":[\"_sp_v1_uid=1:235:774aebaa-ae9e-4a9e-95c5-5c8aec100c8f\",\"_sp_v1_csv=1\",\"_sp_v1_lt=1:\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_data=2:259956:1612170133:0:3:0:3:0:0:753441a5-1e9e-4642-9460-09d36e36fb8a:-1\"],\"messageId\":391194}",
          "propertyHref": "https://tcfv2.mobile.demo",
          "campaignEnv": "public",
          "targetingParams": "{}"
        }
    """.trimIndent()
}
