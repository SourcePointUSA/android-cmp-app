@file:JvmName("LoggerFactory")

package com.sourcepoint.cmplibrary.exception

import android.util.Log
import okhttp3.OkHttpClient

/**
 * Factory method used to create an instance of the [Logger] interface
 * @param networkClient network client
 * @param errorMessageManager entity used to build the network request body
 * @param url server url
 */
fun createLogger(
    networkClient: OkHttpClient,
    errorMessageManager: ErrorMessageManager,
    url: String,
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
    info = info,
    verbose = verbose,
    debug = debug,
    networkClient = networkClient,
    errorMessageManager = errorMessageManager,
    url = url
)

/**
 * Implementation of [Logger]
 */
private class LoggerImpl(
    val info: (tag: String, msg: String) -> Unit = { tag, msg -> Log.i(tag, msg) },
    val debug: (tag: String, msg: String) -> Unit = { tag, msg -> Log.d(tag, msg) },
    val verbose: (tag: String, msg: String) -> Unit = { tag, msg -> Log.v(tag, msg) },
    val pError: (tag: String, msg: String) -> Unit = { tag, msg -> Log.e(tag, msg) },
    val networkClient: OkHttpClient,
    val errorMessageManager: ErrorMessageManager,
    val url: String
) : Logger {
    override fun error(e: ConsentLibExceptionK) { /* No log in debug */ }
    override fun e(tag: String, msg: String) = pError(tag, msg)
    override fun i(tag: String, msg: String) = info(tag, msg)
    override fun d(tag: String, msg: String) = debug(tag, msg)
    override fun v(tag: String, msg: String) = verbose(tag, msg)
}
