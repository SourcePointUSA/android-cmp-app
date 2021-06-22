@file:JvmName("LoggerFactory")

package com.sourcepoint.cmplibrary.exception

import com.sourcepoint.cmplibrary.util.enqueue
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

/**
 * Factory method used to create an instance of the [Logger] interface
 * @param networkClient network client
 * @param errorMessageManager entity used to build the network request body
 * @param url server url
 */
internal fun createLogger(
    networkClient: OkHttpClient,
    errorMessageManager: ErrorMessageManager,
    url: String
): Logger = LoggerImpl(
    networkClient = networkClient,
    errorMessageManager = errorMessageManager,
    url = url
)

/**
 * Factory method used to create an instance of the [Logger] interface for debug purpose
 * @param networkClient network client
 * @param errorMessageManager entity used to build the network request body
 * @param url server url
 */
internal fun createLogger4Testing(
    info: (tag: String, msg: String) -> Unit,
    debug: (tag: String, msg: String) -> Unit,
    verbose: (tag: String, msg: String) -> Unit,
    networkClient: OkHttpClient,
    errorMessageManager: ErrorMessageManager,
    url: String,
): Logger = LoggerImpl(
    networkClient = networkClient,
    errorMessageManager = errorMessageManager,
    url = url
)

/**
 * Implementation of [Logger]
 */
private class LoggerImpl(
    val networkClient: OkHttpClient,
    val errorMessageManager: ErrorMessageManager,
    val url: String
) : Logger {
    override fun error(e: RuntimeException) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, errorMessageManager.build(e))
        val request: Request = Request.Builder().url(url).post(body)
            .header("Accept", mediaType?.type() ?: "")
            .header("Content-Type", mediaType?.type() ?: "")
            .build()

        networkClient.newCall(request).enqueue { }
    }
    override fun e(tag: String, msg: String) { }
    override fun i(tag: String, msg: String) { }
    override fun d(tag: String, msg: String) { }
    override fun v(tag: String, msg: String) { }
    override fun req(tag: String, url: String, type: String, body: String) { }
    override fun res(tag: String, msg: String, status: String, body: String) { }
    override fun actionWebApp(tag: String, msg: String, json: JSONObject?) { }
    override fun clientEvent(tag: String, msg: String, content: String) { }
    override fun computation(tag: String, msg: String) { }
    override fun pm(tag: String, url: String, type: String, pmId: String?) { }
}
