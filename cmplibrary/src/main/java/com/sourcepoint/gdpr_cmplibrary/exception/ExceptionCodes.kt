package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * These codes are use to categorize an exception occurred
 */
object CodeList{
    val INVALID_LOCAL_DATA = ExceptionCodes("INVALID_LOCAL_DATA")
    val INVALID_RESPONSE_WEB_MESSAGE = ExceptionCodes("INVALID_RESPONSE_WEB_MESSAGE")
    val INVALID_RESPONSE_NATIVE_MESSAGE = ExceptionCodes("INVALID_RESPONSE_NATIVE_MESSAGE")
    val INVALID_RESPONSE_CONSENT = ExceptionCodes("INVALID_RESPONSE_CONSENT")
    val INVALID_RESPONSE_CUSTOM_CONSENT = ExceptionCodes("INVALID_RESPONSE_CUSTOM_CONSENT")
    val INVALID_EVENT_PAYLOAD = ExceptionCodes("INVALID_EVENT_PAYLOAD")
    val URL_LOADING_ERROR = ExceptionCodes("URL_LOADING_ERROR")
    val WEB_VIEW_ERROR = ExceptionCodes("WEB_VIEW_ERROR")
    val NO_INTERNET_CONNECTION = ExceptionCodes("NO_INTERNET_CONNECTION")
    val INTERNAL_SERVER_ERROR = ExceptionCodes("INTERNAL_SERVER_ERROR")
    val RESOURCE_NOT_FOUND = ExceptionCodes("RESOURCE_NOT_FOUND")
    val CONNECTION_TIMEOUT = ExceptionCodes("CONNECTION_TIMEOUT")
}

inline class ExceptionCodes(val code : String)