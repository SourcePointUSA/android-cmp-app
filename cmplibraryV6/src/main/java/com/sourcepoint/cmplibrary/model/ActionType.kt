package com.sourcepoint.cmplibrary.model

enum class ActionType(val code: Int) {
    SHOW_OPTIONS(12),
    REJECT_ALL(13),
    ACCEPT_ALL(11),
    MSG_CANCEL(15),
    SAVE_AND_EXIT(1),
    PM_DISMISS(2);
}
