package com.sourcepoint.gdpr_cmplibrary.data.network

import com.example.gdpr_cmplibrary.BuildConfig
import com.sourcepoint.gdpr_cmplibrary.data.executeOnLeft
import com.sourcepoint.gdpr_cmplibrary.data.map
import com.sourcepoint.gdpr_cmplibrary.data.network.model.NativeMessageReq
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

fun createNetworkClient(): NetworkClient = NetworkClientImpl()

private class NetworkClientImpl(
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val url: String = BuildConfig.PRELOADING_URL,
    private val responseManager: ResponseManager = createResponseManager()
) : NetworkClient {

    override fun getNativeMessage(
        nativeMessageReq: NativeMessageReq,
        success: (JSONObject) -> Unit,
        error: (Throwable) -> Unit) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, nativeMessageReq.toBodyRequest())

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