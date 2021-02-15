package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.example.cmplibrary.R
import com.sourcepoint.cmplibrary.core.layout.* // ktlint-disable
import com.sourcepoint.cmplibrary.core.layout.json.NativeMessageDto
import com.sourcepoint.cmplibrary.core.layout.json.toTextViewConfigDto
import com.sourcepoint.cmplibrary.model.ActionType.* // ktlint-disable
import kotlinx.android.synthetic.main.sample_native_message_v6.view.* // ktlint-disable

open class NativeMessageK : NativeMessageAbstract {

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

    override lateinit var cancelAb: ActionButtonK
    override lateinit var acceptAllAb: ActionButtonK
    override lateinit var showOptionsAb: ActionButtonK
    override lateinit var rejectAllAb: ActionButtonK
    override val actionsMap: MutableMap<Int, ActionButtonK> = mutableMapOf()

    override fun initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        View.inflate(context, R.layout.sample_native_message_v6, this)
        if (!this::cancelAb.isInitialized) {
            cancelAb = cancel_btn.toActionButtonK(MSG_CANCEL, ::onCancel, actionsMap)
        }
        if (!this::acceptAllAb.isInitialized) {
            acceptAllAb = accept_all_btn.toActionButtonK(ACCEPT_ALL, ::onAcceptAll, actionsMap)
        }
        if (!this::showOptionsAb.isInitialized) {
            showOptionsAb = show_options_btn.toActionButtonK(SHOW_OPTIONS, ::onShowOptionsAb, actionsMap)
        }
        if (!this::rejectAllAb.isInitialized) {
            rejectAllAb = reject_all_btn.toActionButtonK(REJECT_ALL, ::onRejectAll, actionsMap)
        }
        title_tv.invisible()
        msg_body_tv.invisible()
    }

    /**
     * this set the value from server
     */
    override fun setAttributes(attr: NativeMessageDto) {
        attr.title?.let { title_tv.config(it) }
        attr.body?.let { msg_body_tv.config(it) }
        attr.actions.forEach { actionDto ->
            val s: ActionButtonK? = actionsMap[actionDto.choiceType]
            actionDto.choiceId?.let { s?.choiceId = it.toString() }
            actionDto.choiceType?.let { s?.choiceType = it }
            s?.button?.let { btn ->
                btn.config(actionDto.toTextViewConfigDto())
                btn.visibility = View.VISIBLE
            }
        }
    }
}
