package com.sourcepoint.cmplibrary.core.layout

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.toActionButton
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageCustomImpl
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageDefaultImpl
import com.sourcepoint.cmplibrary.model.ActionType
import kotlinx.android.synthetic.main.sample_native_message_v6.view.*

fun createDefaultNativeMessage(activity: Activity): NativeMessage = NativeMessageDefaultImpl(activity)

fun createCustomNativeMessage(
    activity: Activity,
    layout: Int,
    cancel: Int,
    accept: Int,
    reject: Int,
    show: Int,
    pTitle: Int,
    pBody: Int
): NativeMessage {

    return object : NativeMessageCustomImpl(activity) {

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

            val groupView = View.inflate(context, layout, this)

            cancelBtn = groupView.findViewById(cancel)
            acceptAllBtn = groupView.findViewById(accept)
            showOptionsBtn = groupView.findViewById(show)
            rejectAllBtn = groupView.findViewById(reject)

            cancelAb = cancelBtn.toActionButton(ActionType.MSG_CANCEL, ::onCancel, actionsMap, View.VISIBLE)
            acceptAllAb = acceptAllBtn.toActionButton(ActionType.ACCEPT_ALL, ::onAcceptAll, actionsMap, View.VISIBLE)
            showOptionsAb = showOptionsBtn.toActionButton(ActionType.SHOW_OPTIONS, ::onShowOptionsAb, actionsMap, View.VISIBLE)
            rejectAllAb = rejectAllBtn.toActionButton(ActionType.REJECT_ALL, ::onRejectAll, actionsMap, View.VISIBLE)
            title = groupView.findViewById(pTitle)
            body = groupView.findViewById(pBody)
        }
    }
}
