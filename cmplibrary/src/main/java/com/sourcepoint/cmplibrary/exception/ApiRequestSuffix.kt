package com.sourcepoint.cmplibrary.exception

internal enum class ApiRequestSuffix(
    val apiSuffix: String
) {
    META_DATA("_meta-data"),
    CONSENT_STATUS("_consent-status"),
    MESSAGES("_messages"),
    PV_DATA("_pv-data"),
    GET_CHOICE("_get"),
    POST_CHOICE_GDPR("_post_gdpr"),
    POST_CHOICE_CCPA("_post_ccpa"),
}
