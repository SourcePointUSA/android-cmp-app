package com.sourcepoint.cmplibrary.model.exposed

import kotlinx.serialization.Serializable

enum class ActionType(val code: Int) {
    SHOW_OPTIONS(12),
    REJECT_ALL(13),
    ACCEPT_ALL(11),
    MSG_CANCEL(15),
    CUSTOM(9),
    SAVE_AND_EXIT(1),
    PM_DISMISS(2),
    GET_MSG_ERROR(-2),
    GET_MSG_NOT_CALLED(-3),
    UNKNOWN(-1);
}

enum class NativeMessageActionType(val code: Int) {
    REJECT_ALL(13),
    SHOW_OPTIONS(12),
    ACCEPT_ALL(11),
    MSG_CANCEL(15),
    GET_MSG_ERROR(-2),
    GET_MSG_NOT_CALLED(-3),
    UNKNOWN(-1),
}

@Serializable
enum class MessageSubCategory(val code: Int) {
    TCFv2(5),
    NATIVE_IN_APP(6),
    OTT(7),
    NATIVE_OTT(14)
}

@Serializable
enum class MessageCategory(val code: Int) {
    GDPR(1),
    CCPA(2),
    USNAT(6),
}
