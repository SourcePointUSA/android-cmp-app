package com.sourcepoint.cmplibrary.exception

internal enum class ApiRequestSuffix(
    val apiSuffix: String
) {
    META_DATA("_meta-data"),
    CONSENT_STATUS("_consent-status"),
    MESSAGES("_messages"),
    PV_DATA("_pv-data"),
    GET_CHOICE("_get-choice"),
    POST_CHOICE_GDPR("_post-choice_gdpr"),
    POST_CHOICE_CCPA("_post-choice_ccpa"),
}
