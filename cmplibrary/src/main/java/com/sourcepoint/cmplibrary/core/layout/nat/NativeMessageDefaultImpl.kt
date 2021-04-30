package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.cmplibrary.R
import com.sourcepoint.cmplibrary.core.layout.* // ktlint-disable
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.model.toActionButton
import com.sourcepoint.cmplibrary.core.layout.model.toTextViewConfigDto
import com.sourcepoint.cmplibrary.model.exposed.ActionType.* // ktlint-disable
import kotlinx.android.synthetic.main.sample_native_message_v6.view.* // ktlint-disable

internal class NativeMessageDefaultImpl : NativeMessageInternal {

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

    override lateinit var title: TextView
    override lateinit var body: TextView

    override lateinit var cancelBtn: Button
    override lateinit var acceptAllBtn: Button
    override lateinit var showOptionsBtn: Button
    override lateinit var rejectAllBtn: Button

    override fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        View.inflate(context, R.layout.sample_native_message_v6, this)

        cancelBtn = cancel_btn
        acceptAllBtn = accept_all_btn
        showOptionsBtn = show_options_btn
        rejectAllBtn = reject_all_btn

        cancelAb = cancelBtn.toActionButton(MSG_CANCEL, ::onCancel, actionsMap)
        acceptAllAb = acceptAllBtn.toActionButton(ACCEPT_ALL, ::onAcceptAll, actionsMap)
        showOptionsAb = showOptionsBtn.toActionButton(SHOW_OPTIONS, ::onShowOptionsAb, actionsMap)
        rejectAllAb = rejectAllBtn.toActionButton(REJECT_ALL, ::onRejectAll, actionsMap)
        title = title_tv.apply { invisible() }
        body = msg_body_tv.apply { invisible() }
    }

    /**
     * this set the value from server
     */
    override fun setAttributes(attr: NativeMessageDto) {
        attr.title?.let { title_tv.config(it) }
        attr.body?.let { msg_body_tv.config(it) }
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
