package com.sourcepoint.cmplibrary.exception

import com.sourcepoint.mobile_core.models.SPAction

/**
 * Generic exception class for common lib errors
 */
sealed class ConsentLibExceptionK(
    val description: String,
    cause: Throwable? = null
) : RuntimeException(description, cause) {
    abstract val code: String
}

/**
 * This exception is thrown when we don't have connection
 */
class NoInternetConnectionException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = "No Internet connection."
) : ConsentLibExceptionK(cause = cause, description = description) {
    override val code = "sp_metric_no_internet_errpr"
}

/**
 * This exception is thrown when a JS on error is called
 */
class RenderingAppException @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = "Rendering app called onError js interface",
) : ConsentLibExceptionK(cause = cause, description = description) {
    override val code = "sp_metric_rendering_app_error"
}

class FailedToLoadMessages @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = "loadMessages failed due to ${cause?.message}",
) : ConsentLibExceptionK(description) {
    override val code = "sp_metric_failed_to_load_messages"
}

class ReportActionException @JvmOverloads constructor(
    cause: Throwable? = null,
    action: SPAction?,
    description: String = "Failed while reporting user consent action ${action?.type} due to ${cause?.message}",
) : ConsentLibExceptionK(cause = cause, description = description) {
    override val code = "sp_metric_failed_action_${action?.type?.name ?: "no_type"}_${action?.campaignType?.name ?: "no_legislation"}"
}

class UnableToDownloadRenderingApp @JvmOverloads constructor(
    cause: Throwable? = null,
    url: String,
    description: String = "Unable to download rendering app from $url. ${cause?.message}",
) : ConsentLibExceptionK(description) {
    override val code = "sp_metric_failed_to_download_rendering_app"
}

class UnableToLoadRenderingApp @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = "Unable to load rendering app. ${cause?.message}",
) : ConsentLibExceptionK(description) {
    override val code = "sp_metric_failed_to_load_rendering_app"
}

class FailedToPostCustomConsent @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = "Unable to post custom consent. ${cause?.message}",
) : ConsentLibExceptionK(description) {
    override val code = "sp_metric_failed_to_post_custom_consent"
}

class FailedToDeleteCustomConsent @JvmOverloads constructor(
    cause: Throwable? = null,
    description: String = "Unable to delete custom consent. Cause: ${cause?.message}",
) : ConsentLibExceptionK(description) {
    override val code = "sp_metric_failed_to_delete_custom_consent"
}

class NoIntentFoundForUrl @JvmOverloads constructor(
    val url: String?,
    description: String = "Couldn't an Intent to handle the url $url",
) : ConsentLibExceptionK(description) {
    override val code = "sp_metric_no_intent_found_for_url"
}
