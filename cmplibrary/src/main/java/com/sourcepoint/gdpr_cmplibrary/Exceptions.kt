package com.sourcepoint.gdpr_cmplibrary

internal sealed class ConsentLibExceptionK(
    val description: String,
    cause: Throwable? = null
) : RuntimeException(cause){
    abstract val code: String
}

internal class NoInternetConnectionException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "no_internet_connection"
}

internal class InternalServerException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "internal_server_error"
}

internal class WebViewException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "web_view_error"
}

internal class UrlLoadingException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "url_loading_error"
}

internal class InvalidEventPayloadException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "invalid_event_payload"
}

internal class ResourceNotFoundException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "resource_not_found"
}

internal class InvalidResponseException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "invalid_response"
}

internal class InvalidLocalDataException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "invalid_local_data"
}

internal class ConnectionTimeoutException(
    cause: Throwable? = null,
    description: String
) : ConsentLibExceptionK(
    cause = cause,
    description = description){
    override val code: String = "connection_timeout"
}

