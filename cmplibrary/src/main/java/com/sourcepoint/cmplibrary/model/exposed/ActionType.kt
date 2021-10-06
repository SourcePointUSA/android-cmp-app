package com.sourcepoint.cmplibrary.model.exposed

enum class ActionType(val code: Int) {
    SHOW_OPTIONS(12),
    REJECT_ALL(13),
    ACCEPT_ALL(11),
    MSG_CANCEL(15),
    CONSENT_PAYWALL(16),
    SAVE_AND_EXIT(1),
    PM_DISMISS(2),
    UNKNOWN(-1);
}

enum class NativeMessageActionType(val code: Int) {
    REJECT_ALL(13),
    SHOW_OPTIONS(12),
    ACCEPT_ALL(11),
    MSG_CANCEL(15),
    UNKNOWN(-1),
}

enum class MessageSubCategory(val code: Int) {
    TCFv2(5),
    NATIVE_IN_APP(6)
}
