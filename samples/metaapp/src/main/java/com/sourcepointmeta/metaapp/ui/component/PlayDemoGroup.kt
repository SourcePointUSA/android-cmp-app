package com.sourcepointmeta.metaapp.ui.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import com.google.android.material.chip.Chip
import com.sourcepointmeta.metaapp.R

class PlayDemoGroup : FrameLayout {

//    private val binding: PropertyItemBinding = PropertyItemBinding
//        .inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

//    override fun onFinishInflate() {
//        super.onFinishInflate()
//        val spec = CircularProgressIndicatorSpec(context, /*attrs=*/null, 0)
//        val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(context, spec)
//        binding.catProgressIndicatorChip?.chipIcon = progressIndicatorDrawable
//    }
}

var PlayDemoGroup.saving: Boolean
    set(value) {
        when (value) {
            true -> {
                findViewById<Button>(R.id.play_demo_btn).visibility = View.GONE
                findViewById<Chip>(R.id.cat_progress_indicator_chip).visibility = View.VISIBLE
            }
            false -> {
                findViewById<Button>(R.id.play_demo_btn).visibility = View.VISIBLE
                findViewById<Chip>(R.id.cat_progress_indicator_chip).visibility = View.GONE
            }
        }
    }
    get() {
        return findViewById<Button>(R.id.play_demo_btn).visibility == View.VISIBLE
    }
