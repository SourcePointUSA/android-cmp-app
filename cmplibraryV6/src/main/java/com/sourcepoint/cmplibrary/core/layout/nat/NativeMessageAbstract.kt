package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.cmplibrary.R
import com.sourcepoint.cmplibrary.core.layout.ActionButtonK
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.attribute.AttributeK
import com.sourcepoint.cmplibrary.core.layout.toActionButtonK
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageRespK
import com.sourcepoint.cmplibrary.model.ActionType
import kotlinx.android.synthetic.main.sample_native_message.view.*

abstract class NativeMessageAbstract : RelativeLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract var client: NativeMessageClient

    abstract var cancelAb: ActionButtonK
    abstract var acceptAllAb: ActionButtonK
    abstract var showOptionsAb: ActionButtonK
    abstract var rejectAllAb: ActionButtonK

    abstract fun init()

    abstract fun setAttributes(att: NativeMessageRespK)

    abstract fun initButtons(layout : Int, close : Int, accept : Int, reject : Int, show : Int)

//    fun setChildAttributes(v: ActionButtonK, attr: com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs.Action) {
//        setChildAttributes(v.button, attr)
//        v.choiceId = attr.choiceId
//        v.choiceType = attr.choiceType
//    }

    abstract fun setChildAttributes(v: TextView, attr: AttributeK)
//    {
//        v.visibility = View.VISIBLE
//        v.text = attr.text
//        v.setTextColor(attr.style.color)
//        v.textSize = attr.style.fontSize.toFloat()
//        v.setBackgroundColor(attr.style.backgroundColor)
//    }

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
