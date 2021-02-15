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

open class NativeMessageK : NativeMessageAbstract {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override lateinit var client: NativeMessageClient

    override lateinit var cancelAb: ActionButtonK
    override lateinit var acceptAllAb: ActionButtonK
    override lateinit var showOptionsAb: ActionButtonK
    override lateinit var rejectAllAb: ActionButtonK

    override fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            id = View.generateViewId()
        }
        View.inflate(context, R.layout.sample_native_message, this)
        if(!this::cancelAb.isInitialized) { cancelAb = cancel_btn.toActionButtonK(::onCancel) }
        if(!this::acceptAllAb.isInitialized) { acceptAllAb = accept_all_btn.toActionButtonK(::onAcceptAll) }
        if(!this::showOptionsAb.isInitialized) { showOptionsAb = show_options_btn.toActionButtonK(::onShowOptionsAb) }
        if(!this::rejectAllAb.isInitialized) { rejectAllAb = reject_all_btn.toActionButtonK(::onRejectAll) }
//        title_tv.invisible()
//        msg_body_tv.invisible()
    }

    override fun setAttributes(att: NativeMessageRespK) {

    }

    override fun initButtons(layout : Int, close : Int, accept : Int, reject : Int, show : Int){

    }

//    fun setChildAttributes(v: ActionButtonK, attr: com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs.Action) {
//        setChildAttributes(v.button, attr)
//        v.choiceId = attr.choiceId
//        v.choiceType = attr.choiceType
//    }

    override fun setChildAttributes(v: TextView, attr: AttributeK) {
        v.visibility = View.VISIBLE
        v.text = attr.text
        v.setTextColor(attr.style.color)
        v.textSize = attr.style.fontSize.toFloat()
        v.setBackgroundColor(attr.style.backgroundColor)
    }

}
