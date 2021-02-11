package com.sourcepoint.cmplibrary.core.layout

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.example.cmplibrary.R
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ActionType
import kotlinx.android.synthetic.main.sample_native_message.view.*  // ktlint-disable

class NativeMessageK : RelativeLayout {

    constructor(context: Context?) : super(context) { init() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    lateinit var client: NativeMessageClient

    private val cancelAb by lazy { cancel_btn.toActionButtonK() }
    private val acceptAllAb by lazy { accept_all_btn.toActionButtonK() }
    private val showOptionsAb by lazy { show_options_btn.toActionButtonK() }
    private val rejectAllAb by lazy { reject_all_btn.toActionButtonK() }

    fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        View.inflate(context, R.layout.sample_native_message, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        title_tv.invisible()
        msg_body_tv.invisible()
        cancelAb.run {
            val action = ConsentAction(actionType = ActionType.MSG_CANCEL, choiceId = this.choiceId, requestFromPm = false, saveAndExitVariables = null)
            button.setOnClickListener { client.onClickCancel(this@NativeMessageK, action) }
        }
        acceptAllAb.run {
            val action = ConsentAction(actionType = ActionType.ACCEPT_ALL, choiceId = this.choiceId, requestFromPm = false, saveAndExitVariables = null)
            button.setOnClickListener { client.onClickAcceptAll(this@NativeMessageK, action) }
        }
        showOptionsAb.run {
            val action = ConsentAction(actionType = ActionType.SHOW_OPTIONS, choiceId = this.choiceId, requestFromPm = false, saveAndExitVariables = null)
            button.setOnClickListener { client.onClickShowOptions(this@NativeMessageK, action) }
        }
        rejectAllAb.run {
            val action = ConsentAction(actionType = ActionType.REJECT_ALL, choiceId = this.choiceId, requestFromPm = false, saveAndExitVariables = null)
            button.setOnClickListener { client.onClickRejectAll(this@NativeMessageK, action) }
        }
    }
}
