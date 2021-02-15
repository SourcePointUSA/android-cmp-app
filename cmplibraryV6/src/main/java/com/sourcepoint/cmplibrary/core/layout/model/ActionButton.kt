package com.sourcepoint.cmplibrary.core.layout.model

import android.view.View
import android.widget.Button
import com.sourcepoint.cmplibrary.model.ActionType

data class ActionButton(
    var button: Button,
    var choiceType: Int = -1,
    var choiceId: String = "-1"
)

fun Button.toActionButton(
    actionType: ActionType,
    listener: (ActionButton) -> Unit,
    map: MutableMap<Int, ActionButton>? = null,
    visibilityType: Int = View.INVISIBLE
): ActionButton {
    visibility = visibilityType
    return ActionButton(
        button = this,
        choiceType = actionType.code
    )
        .apply {
            button.setOnClickListener { listener(this) }
            map?.put(actionType.code, this)
        }
}
