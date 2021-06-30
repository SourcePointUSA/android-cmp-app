package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.sourcepointmeta.metaapp.R
import kotlinx.android.synthetic.main.property_item.view.*

class PlayDemoGroup : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        val spec = CircularProgressIndicatorSpec(context, /*attrs=*/null, 0, R.style.Widget_MaterialComponents_CircularProgressIndicator_ExtraSmall)
        val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(context, spec)
        cat_progress_indicator_chip.chipIcon = progressIndicatorDrawable
    }
}

var PlayDemoGroup.saving: Boolean
    set(value) {
        when (value) {
            true -> {
                play_demo_btn.visibility = View.GONE
                cat_progress_indicator_chip.visibility = View.VISIBLE
            }
            false -> {
                play_demo_btn.visibility = View.VISIBLE
                cat_progress_indicator_chip.visibility = View.GONE
            }
        }
    }
    get() {
        return play_demo_btn.visibility == View.VISIBLE
    }
