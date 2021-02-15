package com.sourcepoint.cmplibrary.core.layout.nat

import android.content.Context
import android.util.AttributeSet
import com.sourcepoint.cmplibrary.core.layout.ActionButtonK
import com.sourcepoint.cmplibrary.core.layout.NativeMessageClient
import com.sourcepoint.cmplibrary.core.layout.json.NativeMessageDto

abstract class NativeMessageCustom : NativeMessageAbstract {

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

    override fun setAttributes(attr: NativeMessageDto) {
    }
}
