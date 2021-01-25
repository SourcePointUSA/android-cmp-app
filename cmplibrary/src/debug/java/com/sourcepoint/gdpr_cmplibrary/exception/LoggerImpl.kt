@file:JvmName("LoggerFactory")

package com.sourcepoint.gdpr_cmplibrary.exception

import com.example.gdpr_cmplibrary.BuildConfig
import com.sourcepoint.gdpr_cmplibrary.enqueue
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
    url: String,
): Logger = LoggerImpl(networkClient, errorMessageManager, url)

/**
 * Implementation of [Logger]
 */
private class LoggerImpl(
    val networkClient: OkHttpClient,
    val errorMessageManager: ErrorMessageManager,
    val url: String
) : Logger {
    override fun error(e: ConsentLibExceptionK) {
        // No log in debug
    }
}