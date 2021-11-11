package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.util.AttributeSet
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.exposed.ActionType

abstract class NativeMessageInternal : NativeMessage {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract var client: NativeMessageClient
    abstract var cancelAb: ActionButton
    abstract var acceptAllAb: ActionButton
    abstract var showOptionsAb: ActionButton
    abstract var rejectAllAb: ActionButton

    internal val actionsMap: MutableMap<Int, ActionButton> = mutableMapOf()

    abstract fun initialize()

    abstract fun setAttributes(attr: NativeMessageDto)

    internal open fun setActionClient(pClient: NativeMessageClient) {
        client = pClient
    }

    internal open fun onCancel(ab: ActionButton) {
        val action = ConsentActionImpl(
            actionType = ActionType.MSG_CANCEL,
            choiceId = ab.choiceId,
            requestFromPm = false,
            campaignType = CampaignType.GDPR
        )
        client.onClickCancel(this@NativeMessageInternal, action)
    }

    internal open fun onAcceptAll(ab: ActionButton) {
        val action = ConsentActionImpl(
            actionType = ActionType.ACCEPT_ALL,
            choiceId = ab.choiceId,
            requestFromPm = false,
            campaignType = CampaignType.GDPR
        )
        client.onClickAcceptAll(this@NativeMessageInternal, action)
    }

    internal open fun onRejectAll(ab: ActionButton) {
        val action = ConsentActionImpl(
            actionType = ActionType.REJECT_ALL,
            choiceId = ab.choiceId,
            requestFromPm = false,
            campaignType = CampaignType.GDPR
        )
        client.onClickRejectAll(this@NativeMessageInternal, action)
    }

    internal open fun onShowOptionsAb(ab: ActionButton) {
        val action = ConsentActionImpl(
            actionType = ActionType.SHOW_OPTIONS,
            choiceId = ab.choiceId,
            requestFromPm = false,
            campaignType = CampaignType.GDPR
        )
        client.onClickShowOptions(this@NativeMessageInternal, action)
    }
}
