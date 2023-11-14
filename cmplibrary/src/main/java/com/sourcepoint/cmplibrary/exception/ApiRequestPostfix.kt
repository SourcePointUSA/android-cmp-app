package com.sourcepoint.cmplibrary.exception

internal enum class ApiRequestPostfix(
    val apiPostfix: String
) {
    META_DATA("_meta-data"),
    CONSENT_STATUS("_consent-status"),
    MESSAGES("_messages"),
    PV_DATA("_pv-data"),
    GET_CHOICE("_get-choice"),
    POST_CHOICE_GDPR("_post-choice_gdpr"),
    POST_CHOICE_CCPA("_post-choice_ccpa"),
    POST_CHOICE_USNAT("_post-choice_usnat"),
}
