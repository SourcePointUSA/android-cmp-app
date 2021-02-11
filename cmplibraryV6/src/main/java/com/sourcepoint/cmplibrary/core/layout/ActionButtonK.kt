package com.sourcepoint.cmplibrary.core.layout

import android.view.View
import android.widget.Button

data class ActionButtonK(
    var button: Button,
    var choiceType: Int = -1,
    var choiceId: String = "-1"
)

fun Button.toActionButtonK(): ActionButtonK {
    visibility = View.INVISIBLE
    return ActionButtonK(this)
}
