package com.sourcepoint.gdpr_cmplibrary.exception

internal sealed class ConsentLibExceptionK(
    val description: String,
    cause: Throwable? = null
) : RuntimeException(cause) {
    abstract val code: String
}

internal class NoInternetConnectionException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "no_internet_connection"
}

internal class InternalServerException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "internal_server_error"
}

internal class WebViewException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "web_view_error"
}

internal class UrlLoadingException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "url_loading_error"
}

internal class InvalidEventPayloadException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "invalid_event_payload"
}

internal class ResourceNotFoundException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "resource_not_found"
}

internal class InvalidResponseException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "invalid_response"
}

internal class InvalidLocalDataException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "invalid_local_data"
}

internal class ConnectionTimeoutException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description) {
    override val code: String = "connection_timeout"
}

