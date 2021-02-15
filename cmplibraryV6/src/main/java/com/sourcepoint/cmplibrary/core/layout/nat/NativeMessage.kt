package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import android.widget.TextView
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ActionType

abstract class NativeMessage : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract var client: NativeMessageClient
    abstract var cancelAb: ActionButton
    abstract var acceptAllAb: ActionButton
    abstract var showOptionsAb: ActionButton
    abstract var rejectAllAb: ActionButton

    abstract var title: TextView
    abstract var body: TextView

    internal val actionsMap: MutableMap<Int, ActionButton> = mutableMapOf()

    abstract fun initialize()

    abstract fun setAttributes(attr: NativeMessageDto)

    internal open fun setActionClient(pClient: NativeMessageClient) {
        client = pClient
    }

    internal open fun onCancel(ab: ActionButton) {
        val action = ConsentAction(actionType = ActionType.MSG_CANCEL, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickCancel(this@NativeMessage, action)
    }

    internal open fun onAcceptAll(ab: ActionButton) {
        val action = ConsentAction(actionType = ActionType.ACCEPT_ALL, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickAcceptAll(this@NativeMessage, action)
    }

    internal open fun onRejectAll(ab: ActionButton) {
        val action = ConsentAction(actionType = ActionType.REJECT_ALL, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickRejectAll(this@NativeMessage, action)
    }

    internal open fun onShowOptionsAb(ab: ActionButton) {
        val action = ConsentAction(actionType = ActionType.SHOW_OPTIONS, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickShowOptions(this@NativeMessage, action)
    }
}
