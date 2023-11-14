package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.model.exposed.ActionType

internal fun ActionType.isAcceptOrRejectAll(): Boolean {
    return this == ActionType.ACCEPT_ALL || this == ActionType.REJECT_ALL
}
