package com.sourcepoint.gdpr_cmplibrary.exception

/**
 * These codes are use to categorize an exception occurred
 */
enum class ExceptionCodes {
    CONNECTION_TIMEOUT,
    INVALID_LOCAL_DATA,
    INVALID_RESPONSE,
    RESOURCE_NOT_FOUND,
    INVALID_EVENT_PAYLOAD,
    URL_LOADING_ERROR,
    WEB_VIEW_ERROR,
    INTERNAL_SERVER_ERROR,
    NO_INTERNET_CONNECTION
}