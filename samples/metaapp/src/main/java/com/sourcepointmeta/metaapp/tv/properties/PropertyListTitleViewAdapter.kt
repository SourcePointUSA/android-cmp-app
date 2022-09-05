package com.sourcepointmeta.metaapp.tv.properties

import android.view.View
import android.widget.TextView
import androidx.leanback.widget.TitleViewAdapter

class PropertyListTitleViewAdapter(
    var titleView: TextView
) : TitleViewAdapter() {
    override fun getSearchAffordanceView(): View? = null
    override fun setTitle(titleText: CharSequence?) {
        titleView.text = titleText
        super.setTitle(titleText)
    }
}
