package com.sourcepoint.cmplibrary.exception

/**
 * Class used to send message contain the error details to the server
 */
interface Logger {
    /**
     * The [error] method receives contains the logic to communicate with the server
     * it is used only in production
     * @param e instance of [ConsentLibExceptionK]
     */
    fun error(e: ConsentLibExceptionK)

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
    companion object
}
