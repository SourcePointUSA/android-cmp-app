package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.sourcepoint.cmplibrary.core.layout.ActionButtonK
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.json.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ActionType

abstract class NativeMessageAbstract : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract var client: NativeMessageClient

    abstract var cancelAb: ActionButtonK
    abstract var acceptAllAb: ActionButtonK
    abstract var showOptionsAb: ActionButtonK
    abstract var rejectAllAb: ActionButtonK
    abstract val actionsMap: MutableMap<Int, ActionButtonK>

    abstract fun initialize()

    abstract fun setAttributes(attr: NativeMessageDto)

    internal open fun setActionClient(pClient: NativeMessageClient) {
        client = pClient
    }

    internal open fun onCancel(ab: ActionButtonK) {
        val action = ConsentAction(actionType = ActionType.MSG_CANCEL, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickCancel(this@NativeMessageAbstract, action)
    }

    internal open fun onAcceptAll(ab: ActionButtonK) {
        val action = ConsentAction(actionType = ActionType.ACCEPT_ALL, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickAcceptAll(this@NativeMessageAbstract, action)
    }

    internal open fun onRejectAll(ab: ActionButtonK) {
        val action = ConsentAction(actionType = ActionType.REJECT_ALL, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickRejectAll(this@NativeMessageAbstract, action)
    }

    internal open fun onShowOptionsAb(ab: ActionButtonK) {
        val action = ConsentAction(actionType = ActionType.SHOW_OPTIONS, choiceId = ab.choiceId, requestFromPm = false, saveAndExitVariables = null)
        client.onClickShowOptions(this@NativeMessageAbstract, action)
    }
}
