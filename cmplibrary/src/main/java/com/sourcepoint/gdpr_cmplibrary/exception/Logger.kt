package com.sourcepoint.gdpr_cmplibrary.exception

import com.sourcepoint.gdpr_cmplibrary.enqueue
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Class used to send message contain the error details to the server
 */
internal interface Logger {
    /**
     * The [error] method receives contains the logic to communicate with the server
     * @param url server url
     * @param e instance of [ConsentLibExceptionK]
     */
    fun error(url: String, e: ConsentLibExceptionK)
    companion object
}

/**
 * Factory method used to create an instance of the [Logger] interface
 * @param networkClient network client
 * @param errorMessageManager entity used to build the network request body
 */
internal fun createLogger(
    networkClient: OkHttpClient,
    errorMessageManager: ErrorMessageManager
) : Logger = LoggerImpl(networkClient, errorMessageManager)

/**
 * Implementation of [Logger]
 */
private class LoggerImpl(
    val networkClient: OkHttpClient,
    val errorMessageManager: ErrorMessageManager
) : Logger {
    override fun error(url: String, e: ConsentLibExceptionK) {
        val mediaType = MediaType.parse("application/json")
        val body: RequestBody = RequestBody.create(mediaType, errorMessageManager.build(e))
        val request: Request = Request.Builder().url(url).post(body)
            .header("Accept", mediaType?.type()?:"")
            .header("Content-Type", mediaType?.type()?:"")
            .build()

        networkClient.newCall(request).enqueue { }
    }
}