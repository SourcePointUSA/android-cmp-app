package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * These codes are use to categorize an exception occurred
 */
object CodeList{
    val INVALID_LOCAL_DATA                  = ExceptionCodes("sp_metric_invalid_local_data")
    val INVALID_RESPONSE_WEB_MESSAGE        = ExceptionCodes("sp_metric_invalid_response_web_message")
    val INVALID_RESPONSE_NATIVE_MESSAGE     = ExceptionCodes("sp_metric_invalid_response_native_message")
    val INVALID_RESPONSE_CONSENT            = ExceptionCodes("sp_metric_invalid_response_consent")
    val INVALID_RESPONSE_CUSTOM_CONSENT     = ExceptionCodes("sp_metric_invalid_response_custom_consent")
    val INVALID_EVENT_PAYLOAD               = ExceptionCodes("sp_metric_invalid_event_payload")
    val INVALID_ON_ACTION_EVENT_PAYLOAD     = ExceptionCodes("sp_metric_invalid_onAction_event_payload")
    val URL_LOADING_ERROR                   = ExceptionCodes("sp_metric_url_loading_error")
    val WEB_VIEW_ERROR                      = ExceptionCodes("sp_metric_web_view_error")
    val INTERNAL_SERVER_ERROR               = ExceptionCodes("sp_metric_internal_server_error_5xx")
    val RESOURCE_NOT_FOUND                  = ExceptionCodes("sp_metric_resource_not_found_4xx")
    val CONNECTION_TIMEOUT                  = ExceptionCodes("sp_metric_connection_timeout")
    val GENERIC_NETWORK_REQUEST             = ExceptionCodes("sp_metric_generic_network_request")
    val GENERIC_SDK_ERROR                   = ExceptionCodes("sp_metric_generic_sdk_error")
    val UNABLE_TO_LOAD_JS_RECEIVER          = ExceptionCodes("sp_metric_unable_to_load_jsreceiver")
    val INVALID_REQUEST_ERROR               = ExceptionCodes("sp_metric_invalid_request_error")
}

inline class ExceptionCodes(val code : String)