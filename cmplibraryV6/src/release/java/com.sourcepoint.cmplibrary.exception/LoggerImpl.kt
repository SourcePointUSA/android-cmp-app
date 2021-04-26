@file:JvmName("LoggerFactory")

package com.sourcepoint.cmplibrary.exception

import com.sourcepoint.cmplibrary.util.enqueue
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

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
    override fun error(e: ConsentLibExceptionK) {
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
}
