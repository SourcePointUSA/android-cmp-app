package com.sourcepointmeta.metaapp.tv.cards

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.google.android.material.chip.Chip
import com.sourcepointmeta.metaapp.R

open class TextCardView(context: Context?) : BaseCardView(context) {
    var propertyNameView: TextView? = null
    var messageTypeView: TextView? = null
    var campaignEnvView: TextView? = null
    var accountIdView: TextView? = null
    var chipGDPR: Chip? = null
    var chipCCPA: Chip? = null
    var textView: TextView? = null

    init {
        // Make sure the ImageCardView is focusable.
        val inflater = LayoutInflater.from(getContext())
        inflater.inflate(R.layout.leanback_card_sample, this)

        textView = findViewById(R.id.textView)
    }
    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}