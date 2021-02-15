package com.sourcepoint.cmplibrary.core.layout

import android.view.View
import android.widget.Button
import com.sourcepoint.cmplibrary.model.ActionType

data class ActionButtonK(
    var button: Button,
    var choiceType: Int = -1,
    var choiceId: String = "-1"
)

fun Button.toActionButtonK(
    actionType: ActionType,
    listener: (ActionButtonK) -> Unit,
    map: MutableMap<Int, ActionButtonK>? = null
): ActionButtonK {
    visibility = View.INVISIBLE
    return ActionButtonK(
        button = this,
        choiceType = actionType.code
    )
        .apply {
            button.setOnClickListener { listener(this) }
            map?.put(actionType.code, this)
        }
}
