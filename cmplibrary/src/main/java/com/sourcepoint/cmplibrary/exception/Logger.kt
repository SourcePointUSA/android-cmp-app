package com.sourcepoint.cmplibrary.exception

import org.json.JSONObject

/**
 * Class used to send message contain the error details to the server
 */
interface Logger {
    /**
     * The [error] method receives contains the logic to communicate with the server
     * it is used only in production
     * @param e instance of [ConsentLibExceptionK]
     */
    fun error(e: RuntimeException)

    /**
     * Send an {@link #ERROR} log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun e(tag: String, msg: String)

    /**
     * The [i] method receives contains the logic to communicate with the server
     * it is used only in debug
     * @param tag Used to identify the source of a log message.
     *            It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun i(tag: String, msg: String)

    /**
     * The [d] method receives contains the logic to communicate with the server
     * it is used only in debug
     * @param tag Used to identify the source of a log message.
     *            It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun d(tag: String, msg: String)

    /**
     * The [v] method receives contains the logic to communicate with the server
     * it is used only in debug
     * @param tag Used to identify the source of a log message.
     *            It usually identifies the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun v(tag: String, msg: String)

    fun req(tag: String, url: String, type: String, body: String)

    fun res(tag: String, msg: String, status: String, body: String)

    fun actionWebApp(tag: String, msg: String, json: JSONObject?)

    fun computation(tag: String, msg: String)

    fun clientEvent(event: String, msg: String, content: String)

    fun pm(tag: String, url: String, type: String, pmId: String?)

    companion object
}
