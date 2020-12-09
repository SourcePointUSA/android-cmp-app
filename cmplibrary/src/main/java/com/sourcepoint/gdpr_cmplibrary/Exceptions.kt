package com.sourcepoint.gdpr_cmplibrary

import java.lang.RuntimeException

internal sealed class ConsentLibExceptionK(
    val details: String,
    val code: String,
    val description: String,
    cause: Throwable? = null
) : RuntimeException(cause)

internal class NoInternetConnectionException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class ServerInternalException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class WebViewOnErrorException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class UrlLoadingException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class EventPayloadException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class RemoteConfigException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class ResourceNotFoundException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

internal class ConnectionTimeoutException(
    details: String,
    cause: Throwable? = null
) : ConsentLibExceptionK(
    details = details,
    cause = cause,
    code = "",
    description = "")

