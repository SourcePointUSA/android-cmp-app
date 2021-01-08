package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * Generic exception class for common lib errors
 * @param isConsumed is used to know if the [Throwable] has been logged already
 */
internal sealed class ConsentLibExceptionK(
    var isConsumed : Boolean = false,
    val description: String,
    cause: Throwable? = null
) : RuntimeException(cause) {
    abstract val code: ExceptionCodes
}

/**
 * This exception is thrown when the response from getting the message is invalid
 */
internal class InvalidResponseWebMessageException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_WEB_MESSAGE
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
    override val code: ExceptionCodes = CodeList.INTERNAL_SERVER_ERROR
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
    override val code: ExceptionCodes = CodeList.WEB_VIEW_ERROR
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
    override val code: ExceptionCodes = CodeList.URL_LOADING_ERROR
}

/**
 * This exception is thrown when the event payload coming from the webview is invalid
 */
internal class InvalidEventPayloadException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_EVENT_PAYLOAD
}

/**
 * This exception is thrown when a not expected event payloads is received
 */
internal class InvalidOnActionEventPayloadException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_ON_ACTION_EVENT_PAYLOAD
}

/**
 * This exception is thrown when a JS on error is called
 */
internal class RenderingAppException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    pCode : String
) : ConsentLibExceptionK(
    cause = cause,
    description = description
){
    override val code: ExceptionCodes = ExceptionCodes(pCode)
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
    override val code: ExceptionCodes = CodeList.RESOURCE_NOT_FOUND
}

/**
 * This exception is thrown when the response from getting the web message is invalid
 */
internal class InvalidResponseException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_WEB_MESSAGE
}

/**
 * This exception is thrown when the response from getting the native message is invalid
 */
internal class InvalidResponseNativeMessageException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_NATIVE_MESSAGE
}

/**
 * This exception is thrown when the response from posting consent is invalid
 */
internal class InvalidResponseConsentException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_CONSENT
}

/**
 * This exception is thrown when the response from posting custom consent is invalid
 */
internal class InvalidResponseCustomConsent @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_CUSTOM_CONSENT
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
    override val code: ExceptionCodes = CodeList.INVALID_LOCAL_DATA
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
    override val code: ExceptionCodes = CodeList.CONNECTION_TIMEOUT
}

/**
 * This exception is thrown when a generic network request error occurred
 */
internal class GenericNetworkRequestException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.GENERIC_NETWORK_REQUEST
}

/**
 * This exception is thrown when a generic error occurred
 */
internal class GenericSDKException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.GENERIC_SDK_ERROR
}

/**
 * This exception is thrown when the SDK doesn't have all the necessary data to perform a request.
 */
internal class InvalidRequestException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.INVALID_REQUEST_ERROR
}

/**
 * This exception is thrown when a generic error occurred
 */
internal class UnableToLoadJSReceiverException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: ExceptionCodes = CodeList.UNABLE_TO_LOAD_JS_RECEIVER
}


