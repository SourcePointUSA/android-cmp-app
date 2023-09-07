package com.sourcepoint.cmplibrary.exception

/**
 * Generic exception class for common lib errors
 * @param isConsumed is used to know if the [Throwable] has been logged already
 */
internal sealed class ConsentLibExceptionK(
    var isConsumed: Boolean = false,
    val description: String,
    cause: Throwable? = null
) : RuntimeException(description, cause) {
    abstract val code: ExceptionCodes
}

/**
 * This exception is thrown when the response from getting the message is invalid
 */
internal class InvalidResponseWebMessageException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_WEB_MESSAGE
}

/**
 * This exception is thrown when in the WebView a problem has occurred
 */
internal class WebViewException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.WEB_VIEW_ERROR
}

/**
 * This exception is thrown when in the ViewManager a problem has occurred
 */
internal class WebViewCreationException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.WEB_VIEW_CREATION_ERROR
}

/**
 * This exception is thrown when the WebView cannot load the url received
 */
internal class UrlLoadingException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.URL_LOADING_ERROR
}

/**
 * This exception is thrown when a JS on error is called
 */
internal class RenderingAppException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false,
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.RENDERING_APP_ERROR
}

/**
 * This exception is thrown when the response from the api is invalid
 */
internal class InvalidApiResponseException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false,
    networkCode: String = ""
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes(CodeList.INVALID_RESPONSE_API.errorCode + networkCode)
}

/**
 * This exception is thrown when the response from getting the native message is invalid
 */
internal class InvalidResponseNativeMessageException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.INVALID_RESPONSE_NATIVE_MESSAGE
}

/**
 * This exception is thrown when the propertyName is not properly formatted
 */
internal class InvalidArgumentException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.INVALID_LOCAL_DATA
}

/**
 * This exception is thrown when we receive a request timeout
 */

val TIMEOUT_MESSAGE = "A timeout has occurred when requesting the message data. " +
    "Please check your internet connection. " +
    "You can extend the timeout using the messageTimeout config parameter."

internal class ConnectionTimeoutException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = TIMEOUT_MESSAGE,
    isConsumed: Boolean = false,
    networkCode: String = ""
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes(CodeList.CONNECTION_TIMEOUT.errorCode + networkCode)
}

internal class RenderingAppConnectionTimeoutException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes(CodeList.RENDERING_APP_CONNECTION_TIMEOUT.errorCode)
}

/**
 * This exception is thrown when we don't have connection
 */
internal class NoInternetConnectionException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = ExceptionDescriptions.EXCEPTION_NO_INTERNET_CONNECTION_DESCRIPTION,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes("NO_INTERNET_CONNECTION")
}

/**
 * This exception is thrown when a generic error occurred
 */
internal class GenericSDKException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.GENERIC_SDK_ERROR
}

/**
 * This exception is thrown when the SDK doesn't have all the necessary data to perform a request.
 */
internal class InvalidRequestException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false,
    apiRequestPostfix: String = "",
    choice: String = "",
    httpStatusCode: String = "",
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes(
        errorCode = CodeList.INVALID_REQUEST_ERROR.errorCode + apiRequestPostfix + choice + httpStatusCode
    )
}

/**
 * This exception is thrown when a property is missing
 */
internal class MissingPropertyException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes("MISSING_LOCAL_PROPERTY")
}

/**
 * This exception is thrown when a property is missing
 */
internal class ChildPmIdNotFound @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.CHILD_PM_ID_NOT_FOUND
}

/**
 * This exception is thrown when a the current thread is not the MainThread
 */
internal class ExecutionInTheWrongThreadException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes("OUT_OF_MAIN_THREAD")
}

/**
 * This exception is thrown when the consent params are missing
 */
internal class InvalidConsentResponse @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = CodeList.INVALID_CONSENT_STATUS_RESPONSE
}

/**
 * This exception is thrown when the SDK is not being able to parse the network response
 */
internal class UnableToParseResponseException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false,
    apiRequestPostfix: String = "",
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed,
) {
    override val code: ExceptionCodes =
        ExceptionCodes(CodeList.UNABLE_TO_PARSE_RESPONSE.errorCode + apiRequestPostfix)
}

/**
 * This exception is thrown when the network request failed.
 */
internal class RequestFailedException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String,
    isConsumed: Boolean = false,
    apiRequestPostfix: String = "",
    choice: String = "",
    httpStatusCode: String = "",
) : ConsentLibExceptionK(
    cause = cause,
    description = description,
    isConsumed = isConsumed
) {
    override val code: ExceptionCodes = ExceptionCodes(
        errorCode = CodeList.REQUEST_FAILED.errorCode + apiRequestPostfix + choice + httpStatusCode
    )
}
