@file:JvmName("LoggerFactory")

package com.sourcepoint.cmplibrary.exception

import android.util.Log
import com.sourcepoint.cmplibrary.exception.LoggerType.NL
import okhttp3.OkHttpClient
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
    override fun error(e: RuntimeException) { /* No log in debug */ }
    override fun e(tag: String, msg: String) = pError(tag, msg)
    override fun i(tag: String, msg: String) = info(tag, msg)
    override fun d(tag: String, msg: String) = debug(tag, msg)
    override fun v(tag: String, msg: String) = verbose(tag, msg)
    override fun req(tag: String, url: String, type: String, body: String) = verbose(tag, "$type$NL$url$NL$body")
    override fun res(tag: String, msg: String, status: String, body: String) = verbose(tag, "$msg$NL$url$NL$body")
    override fun actionWebApp(tag: String, msg: String, json: JSONObject?) = verbose(tag, "$msg$NL$json")
    override fun computation(tag: String, msg: String) = verbose(tag, msg)
    override fun clientEvent(event: String, msg: String, content: String) = verbose(event, "$msg$NL$content")
    override fun pm(tag: String, url: String, type: String, pmId: String?) = verbose(tag, "$type$NL$url$NL$pmId")
}
