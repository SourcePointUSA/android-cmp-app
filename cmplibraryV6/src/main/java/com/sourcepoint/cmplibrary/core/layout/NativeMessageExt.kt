package com.sourcepoint.cmplibrary.core.layout

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.sourcepoint.cmplibrary.core.layout.json.TextViewConfigDto

fun View.invisible() {
    visibility = View.INVISIBLE
}

internal fun TextView.config(title: TextViewConfigDto) {
    visibility = View.VISIBLE
    text = title.text
    title.style?.color?.let { setTextColor(Color.parseColor(it)) }
    title.style?.fontSize?.toFloat()?.let { textSize = it }
    title.style?.backgroundColor?.let { setBackgroundColor(Color.parseColor(it)) }
}
