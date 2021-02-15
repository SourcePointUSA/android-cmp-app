package com.sourcepoint.cmplibrary.core.layout.nat

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.Button
import com.sourcepoint.cmplibrary.core.layout.ActionButtonK
import com.sourcepoint.cmplibrary.core.layout.invisible
import com.sourcepoint.cmplibrary.core.layout.toActionButtonK

fun createNativeMessage(
    activity: Activity,
    layout: Int,
    close: Int,
    accept: Int,
    reject: Int,
    show: Int,
    title: Int,
    body: Int
) : NativeMessageAbstract {

    return object : NativeMessageCustom(activity) {

        override lateinit var cancelAb: ActionButtonK
        override lateinit var acceptAllAb: ActionButtonK
        override lateinit var showOptionsAb: ActionButtonK
        override lateinit var rejectAllAb: ActionButtonK

        override fun init() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                id = View.generateViewId()
            }
            val groupView = View.inflate(context, layout, this)

            if (!this::cancelAb.isInitialized) {
                cancelAb = groupView.findViewById<Button>(close).toActionButtonK(::onCancel)
            }
            if (!this::acceptAllAb.isInitialized) {
                acceptAllAb = groupView.findViewById<Button>(accept).toActionButtonK(::onAcceptAll)
            }
            if (!this::showOptionsAb.isInitialized) {
                showOptionsAb = groupView.findViewById<Button>(show).toActionButtonK(::onShowOptionsAb)
            }
            if (!this::rejectAllAb.isInitialized) {
                rejectAllAb = groupView.findViewById<Button>(reject).toActionButtonK(::onRejectAll)
            }
            groupView.findViewById<Button>(title).invisible()
            groupView.findViewById<Button>(body).invisible()
        }
    }

}