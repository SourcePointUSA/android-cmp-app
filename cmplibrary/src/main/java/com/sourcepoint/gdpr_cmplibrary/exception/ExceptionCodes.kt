package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * These codes are use to categorize an exception occurred
 */
object CodeList{
    val INVALID_LOCAL_DATA = ExceptionCodes("invalid_local_data")
    val INVALID_RESPONSE_WEB_MESSAGE = ExceptionCodes("invalid_response_web_message")
    val INVALID_RESPONSE_NATIVE_MESSAGE = ExceptionCodes("invalid_response_native_message")
    val INVALID_RESPONSE_CONSENT = ExceptionCodes("invalid_response_consent")
    val INVALID_RESPONSE_CUSTOM_CONSENT = ExceptionCodes("invalid_response_custom_consent")
    val INVALID_EVENT_PAYLOAD = ExceptionCodes("invalid_event_payload")
    val URL_LOADING_ERROR = ExceptionCodes("url_loading_error")
    val WEB_VIEW_ERROR = ExceptionCodes("web_view_error")
    val NO_INTERNET_CONNECTION = ExceptionCodes("no_internet_connection")
    val INTERNAL_SERVER_ERROR = ExceptionCodes("internal_server_error_5xx")
    val RESOURCE_NOT_FOUND = ExceptionCodes("resource_not_found_4xx")
    val CONNECTION_TIMEOUT = ExceptionCodes("connection_timeout")
    val GENERIC_NETWORK_REQUEST = ExceptionCodes("generic_network_request")
}

inline class ExceptionCodes(val code : String)