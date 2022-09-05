package com.sourcepointmeta.metaapp.tv.properties

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.leanback.widget.TitleViewAdapter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.demo.DemoTitleViewAdapter
import kotlinx.android.synthetic.main.property_list_title.view.*

class PropertyListTitleView : ConstraintLayout, TitleViewAdapter.Provider {
    private val titleAdapter by lazy { PropertyListTitleViewAdapter(findViewById(R.id.titleTextView)) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getTitleViewAdapter(): TitleViewAdapter = titleAdapter
}
