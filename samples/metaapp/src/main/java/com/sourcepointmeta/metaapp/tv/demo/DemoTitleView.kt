package com.sourcepointmeta.metaapp.tv.demo

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.TitleViewAdapter

class DemoTitleView : ConstraintLayout, TitleViewAdapter.Provider {

    private val titleAdapter by lazy { DemoTitleViewAdapter() }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getTitleViewAdapter(): TitleViewAdapter = titleAdapter
}
