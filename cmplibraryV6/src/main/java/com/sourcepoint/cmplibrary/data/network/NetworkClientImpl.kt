package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.util.executeOnLeft
import com.sourcepoint.cmplibrary.util.map
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

fun createNetworkClient(): NetworkClient = NetworkClientImpl()

private class NetworkClientImpl(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val url: String = "",
    private val responseManager: ResponseManager = createResponseManager()
) : NetworkClient {

    override fun getNativeMessage(
        uwReq: UWReq,
        success: (UWResp) -> Unit,
        error: (Throwable) -> Unit) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, uwReq.toBodyRequest())

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        httpClient
            .newCall(request)
            .enqueue {
                onFailure { _, exception -> error(exception) }
                onResponse { _, r ->
                    responseManager
                        .parseResponse(r)
                        .map { success(it) }
                        .executeOnLeft { error(it) }
                }
            }
    }
}