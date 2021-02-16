package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.example.cmplibrary.R
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.config
import com.sourcepoint.cmplibrary.core.layout.invisible
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toActionButton
import com.sourcepoint.cmplibrary.core.layout.model.toTextViewConfigDto
import com.sourcepoint.cmplibrary.model.ActionType
import kotlinx.android.synthetic.main.sample_native_message_v6.view.*

internal abstract class NativeMessageCustomImpl : NativeMessage {

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    override lateinit var client: NativeMessageClient

    override lateinit var cancelAb: ActionButton
    override lateinit var acceptAllAb: ActionButton
    override lateinit var showOptionsAb: ActionButton
    override lateinit var rejectAllAb: ActionButton

    override fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        View.inflate(context, R.layout.sample_native_message_v6, this)
        if (!this::cancelAb.isInitialized) {
            cancelAb = cancel_btn.toActionButton(ActionType.MSG_CANCEL, ::onCancel, actionsMap)
        }
        if (!this::acceptAllAb.isInitialized) {
            acceptAllAb = accept_all_btn.toActionButton(ActionType.ACCEPT_ALL, ::onAcceptAll, actionsMap)
        }
        if (!this::showOptionsAb.isInitialized) {
            showOptionsAb = show_options_btn.toActionButton(ActionType.SHOW_OPTIONS, ::onShowOptionsAb, actionsMap)
        }
        if (!this::rejectAllAb.isInitialized) {
            rejectAllAb = reject_all_btn.toActionButton(ActionType.REJECT_ALL, ::onRejectAll, actionsMap)
        }
        title = title_tv.apply { invisible() }
        body = msg_body_tv.apply { invisible() }
    }

    /**
     * this set the value from server
     */
    override fun setAttributes(attr: NativeMessageDto) {
        attr.title?.let { title.config(it) }
        attr.body?.let { body.config(it) }
        attr.actions.forEach { actionDto ->
            val s: ActionButton? = actionsMap[actionDto.choiceType]
            actionDto.choiceId?.let { s?.choiceId = it.toString() }
            actionDto.choiceType?.let { s?.choiceType = it }
            s?.button?.let { btn ->
                btn.config(actionDto.toTextViewConfigDto())
                btn.visibility = View.VISIBLE
            }
        }
    }
}
