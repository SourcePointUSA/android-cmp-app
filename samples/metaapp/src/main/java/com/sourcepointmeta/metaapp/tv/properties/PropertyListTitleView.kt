package com.sourcepointmeta.metaapp.tv.properties

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.TitleViewAdapter
import com.sourcepointmeta.metaapp.R

class PropertyListTitleView : ConstraintLayout, TitleViewAdapter.Provider {
    private val titleAdapter by lazy { PropertyListTitleViewAdapter(findViewById(R.id.title_text_view)) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getTitleViewAdapter(): TitleViewAdapter = titleAdapter
}
