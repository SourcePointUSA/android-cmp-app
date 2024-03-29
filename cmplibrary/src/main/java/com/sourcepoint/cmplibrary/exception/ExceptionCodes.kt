package com.sourcepoint.cmplibrary.exception

/**
 * These codes are use to categorize an exception occurred
 */
internal object CodeList {
    val INVALID_LOCAL_DATA = ExceptionCodes("sp_metric_invalid_local_data")
    val INVALID_RESPONSE_WEB_MESSAGE = ExceptionCodes("sp_metric_invalid_response_web_message")
    val INVALID_RESPONSE_API = ExceptionCodes("sp_metric_invalid_response_api")
    val INVALID_RESPONSE_NATIVE_MESSAGE = ExceptionCodes("sp_metric_invalid_response_native_message")
    val URL_LOADING_ERROR = ExceptionCodes("sp_metric_url_loading_error")
    val WEB_VIEW_ERROR = ExceptionCodes("sp_metric_web_view_error")
    val WEB_VIEW_CREATION_ERROR = ExceptionCodes("sp_metric_webview_creation_error")
    val CONNECTION_TIMEOUT = ExceptionCodes("sp_metric_connection_timeout")
    val RENDERING_APP_CONNECTION_TIMEOUT = ExceptionCodes("sp_metric_rendering_app_timeout")
    val GENERIC_SDK_ERROR = ExceptionCodes("sp_metric_generic_sdk_error")
    val INVALID_REQUEST_ERROR = ExceptionCodes("sp_metric_invalid_request_error")
    val CHILD_PM_ID_NOT_FOUND = ExceptionCodes("sp_log_child_pm_id_custom_metrics")
    val INVALID_CONSENT_STATUS_RESPONSE = ExceptionCodes("sp_metric_invalid_consent_status_response")
    val RENDERING_APP_ERROR = ExceptionCodes("sp_metric_rendering_app_error")
    val UNABLE_TO_PARSE_RESPONSE = ExceptionCodes("sp_metric_unable_to_parse_response")
    val REQUEST_FAILED = ExceptionCodes("sp_metric_request_failed")
}

internal inline class ExceptionCodes(val errorCode: String)
