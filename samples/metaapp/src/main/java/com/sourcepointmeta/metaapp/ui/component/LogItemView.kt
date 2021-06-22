package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import com.sourcepointmeta.metaapp.R

class LogItemView : ConstraintLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val lightItem: Drawable by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorOddItem, this, true) }
            .data
            .toDrawable()
    }

    val darkItem: Drawable by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorEvenItem, this, true) }
            .data
            .toDrawable()
    }
}
