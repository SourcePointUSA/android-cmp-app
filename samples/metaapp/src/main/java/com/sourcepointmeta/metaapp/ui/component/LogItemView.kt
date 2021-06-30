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

    val responseSuccessColorDrawable: Drawable by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorSuccessResponse, this, true) }
            .data
            .toDrawable()
    }

    val responseErrorColorDrawable: Drawable by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
            .toDrawable()
    }

    val responseSuccessColor: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorSuccessResponse, this, true) }
            .data
    }

    val responseErrorColor: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }

    val colorLink: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.webLink, this, true) }
            .data
    }

    val colorTextOnSurface: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorOnSurface, this, true) }
            .data
    }

    val colorClientEvent: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorClientEvent, this, true) }
            .data
    }
}
