package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.util.AttributeSet
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.model.ActionButton
import com.sourcepoint.cmplibrary.core.layout.model.NativeMessageDto

internal abstract class NativeMessageCustomImpl : NativeMessage {

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

    override fun setAttributes(attr: NativeMessageDto) {
    }
}
