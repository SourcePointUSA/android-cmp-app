package com.sourcepoint.cmplibrary.core.layout

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.Button
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.toActionButton
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessage
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageCustomImpl
import com.sourcepoint.cmplibrary.core.layout.nat.NativeMessageDefaultImpl
import com.sourcepoint.cmplibrary.model.ActionType

fun createDefaultNativeMessage(activity: Activity): NativeMessage = NativeMessageDefaultImpl(activity)

fun createCustomNativeMessage(
    activity: Activity,
    layout: Int,
    cancel: Int,
    accept: Int,
    reject: Int,
    show: Int,
    title: Int,
    body: Int
): NativeMessage {

    return object : NativeMessageCustomImpl(activity) {

        override lateinit var cancelAb: ActionButton
        override lateinit var acceptAllAb: ActionButton
        override lateinit var showOptionsAb: ActionButton
        override lateinit var rejectAllAb: ActionButton

        override fun initialize() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                id = View.generateViewId()
            }
            val groupView = View.inflate(context, layout, this)

            if (!this::cancelAb.isInitialized) {
                cancelAb = groupView.findViewById<Button>(cancel)
                    .toActionButton(ActionType.MSG_CANCEL, ::onCancel, visibilityType = View.VISIBLE)
            }
            if (!this::acceptAllAb.isInitialized) {
                acceptAllAb = groupView.findViewById<Button>(accept)
                    .toActionButton(ActionType.ACCEPT_ALL, ::onAcceptAll, visibilityType = View.VISIBLE)
            }
            if (!this::showOptionsAb.isInitialized) {
                showOptionsAb = groupView.findViewById<Button>(show)
                    .toActionButton(ActionType.SHOW_OPTIONS, ::onShowOptionsAb, visibilityType = View.VISIBLE)
            }
            if (!this::rejectAllAb.isInitialized) {
                rejectAllAb = groupView.findViewById<Button>(reject)
                    .toActionButton(ActionType.REJECT_ALL, ::onRejectAll, visibilityType = View.VISIBLE)
            }
        }
    }
}
