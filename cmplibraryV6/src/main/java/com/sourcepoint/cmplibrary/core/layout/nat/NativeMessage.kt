package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.model.ActionType

abstract class NativeMessage : RelativeLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    abstract var title: TextView
    abstract var body: TextView
    abstract var cancelBtn: Button
    abstract var acceptAllBtn: Button
    abstract var showOptionsBtn: Button
    abstract var rejectAllBtn: Button

}
