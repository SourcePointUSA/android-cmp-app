package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * Class used to send message contain the error details to the server
 */
internal interface Logger {
    /**
     * The [error] method receives contains the logic to communicate with the server
     * @param e instance of [ConsentLibExceptionK]
     */
    fun error(e: ConsentLibExceptionK)
    companion object
}