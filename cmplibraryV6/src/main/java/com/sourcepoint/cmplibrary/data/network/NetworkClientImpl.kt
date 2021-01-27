package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.executeOnLeft
import com.sourcepoint.cmplibrary.util.map
import okhttp3.* // ktlint-disable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal fun createNetworkClient(
    httpClient: OkHttpClient,
    url: HttpUrl,
    responseManager: ResponseManager
): NetworkClient = NetworkClientImpl(httpClient, url, responseManager)

private class NetworkClientImpl(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val url: HttpUrl = HttpUrlManagerSingleton.inAppUrlMessage,
    private val responseManager: ResponseManager = ResponseManager.create(JsonConverter.create())
) : NetworkClient {

    override fun getMessage(
        uwReq: UWReq,
        success: (UWResp) -> Unit,
        error: (Throwable) -> Unit
    ) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, uwReq.toBodyRequest())

        val request: Request = Request.Builder()
            .url(url)
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
                        .parseResponse(r)
                        .map { success(it) }
                        .executeOnLeft { error(it) }
                }
            }
    }

    override suspend fun getMessage(uwReq: UWReq) = suspendCoroutine<Either<UWResp>> {

        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, uwReq.toBodyRequest())

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        httpClient
            .newCall(request)
            .enqueue {
                onFailure { _, exception ->
                    it.resume(Either.Left(exception))
                }
                onResponse { _, r ->
                    it.resume(responseManager.parseResponse(r))
                }
            }
    }
}
