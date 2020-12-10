package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * Generic exception class for common lib errors
 */
internal sealed class ConsentLibExceptionK(
    val description: String,
    cause: Throwable? = null
) : RuntimeException(cause) {
    abstract val code: ExceptionCodes
}

/**
 * This exception is thrown when any network problem has occurred
 */
internal class NoInternetConnectionException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.NO_INTERNET_CONNECTION
}

/**
 * This exception is thrown when we receive a 500 status code in the network response
 */
internal class InternalServerException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.INTERNAL_SERVER_ERROR
}

/**
 * This exception is thrown when in the WebView a problem has occurred
 */
internal class WebViewException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.WEB_VIEW_ERROR
}

/**
 * This exception is thrown when the WebView cannot load the url received
 */
internal class UrlLoadingException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.URL_LOADING_ERROR
}

/**
 * This exception is thrown when a not expected event payloads is received
 */
internal class InvalidEventPayloadException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.INVALID_EVENT_PAYLOAD
}

/**
 * This exception is thrown when some resource cannot be found locally
 */
internal class ResourceNotFoundException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.RESOURCE_NOT_FOUND
}

/**
 * This exception is thrown when a not valid response is received
 */
internal class InvalidResponseException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.INVALID_RESPONSE
}

/**
 * This exception is thrown when some resource cannot be found locally
 */
internal class InvalidLocalDataException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.INVALID_LOCAL_DATA
}

/**
 * This exception is thrown when we receive a request timeout
 */
internal class ConnectionTimeoutException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = ExceptionCodes.CONNECTION_TIMEOUT
}

