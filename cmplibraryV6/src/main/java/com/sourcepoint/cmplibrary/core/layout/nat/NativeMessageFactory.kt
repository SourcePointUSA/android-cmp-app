package com.sourcepoint.cmplibrary.core.layout.nat

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.Button
import com.sourcepoint.cmplibrary.core.layout.ActionButtonK
import com.sourcepoint.cmplibrary.core.layout.invisible
import com.sourcepoint.cmplibrary.core.layout.toActionButtonK
import com.sourcepoint.cmplibrary.model.ActionType
import kotlinx.android.synthetic.main.sample_native_message_v6.view.*

fun createNativeMessage(
    activity: Activity,
    layout: Int,
    close: Int,
    accept: Int,
    reject: Int,
    show: Int,
    title: Int,
    body: Int
): NativeMessageAbstract {

    return object : NativeMessageCustom(activity) {

        override lateinit var cancelAb: ActionButtonK
        override lateinit var acceptAllAb: ActionButtonK
        override lateinit var showOptionsAb: ActionButtonK
        override lateinit var rejectAllAb: ActionButtonK
        override val actionsMap: MutableMap<Int, ActionButtonK> = mutableMapOf()

        override fun initialize() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                id = View.generateViewId()
            }
            val groupView = View.inflate(context, layout, this)

            if (!this::cancelAb.isInitialized) {
                cancelAb = cancel_btn.toActionButtonK(ActionType.MSG_CANCEL, ::onCancel)
            }
            if (!this::acceptAllAb.isInitialized) {
                acceptAllAb = accept_all_btn.toActionButtonK(ActionType.ACCEPT_ALL, ::onAcceptAll)
            }
            if (!this::showOptionsAb.isInitialized) {
                showOptionsAb = show_options_btn.toActionButtonK(ActionType.SHOW_OPTIONS, ::onShowOptionsAb)
            }
            if (!this::rejectAllAb.isInitialized) {
                rejectAllAb = reject_all_btn.toActionButtonK(ActionType.REJECT_ALL, ::onRejectAll)
            }
            groupView.findViewById<Button>(title).invisible()
            groupView.findViewById<Button>(body).invisible()
        }
    }
}
