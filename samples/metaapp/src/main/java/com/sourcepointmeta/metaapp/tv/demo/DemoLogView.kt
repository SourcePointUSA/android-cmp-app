package com.sourcepointmeta.metaapp.tv.demo

import android.content.Context
import android.util.AttributeSet
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.ui.component.LogItem
import kotlinx.android.synthetic.main.log_item.view.*

class DemoLogView : BaseCardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}

fun DemoLogView.bind(item: LogItem) {
    log_event.text = item.tag
}
