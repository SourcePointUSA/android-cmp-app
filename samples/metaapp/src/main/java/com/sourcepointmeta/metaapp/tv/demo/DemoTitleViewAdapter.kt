package com.sourcepointmeta.metaapp.tv.demo

import android.view.View
import android.view.View.OnClickListener
import androidx.leanback.widget.TitleViewAdapter

class DemoTitleViewAdapter : TitleViewAdapter() {
    override fun getSearchAffordanceView(): View? = null

    override fun setOnSearchClickedListener(listener: OnClickListener?) {
        super.setOnSearchClickedListener(listener)
    }

    override fun updateComponentsVisibility(flags: Int) {}
}
