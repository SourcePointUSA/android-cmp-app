package com.sourcepointmeta.metaapp.tv.cards

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.R

open class TextCardView(context: Context?) : BaseCardView(context) {
    val CARD_TYPE_FLAG_IMAGE_ONLY = 0
    val CARD_TYPE_FLAG_TITLE = 1
    val CARD_TYPE_FLAG_CONTENT = 2
    val CARD_TYPE_FLAG_ICON_RIGHT = 4
    val CARD_TYPE_FLAG_ICON_LEFT = 8

    val ALPHA = "alpha"

    var propertyNameView: TextView? = null
    var messageTypeView: TextView? = null
    var campaignEnvView: TextView? = null
    var accountIdView: TextView? = null

    init {
        // Make sure the ImageCardView is focusable.
        isFocusable = false
        isFocusableInTouchMode = false
        val inflater = LayoutInflater.from(getContext())
        inflater.inflate(R.layout.property_item, this)

        propertyNameView = findViewById(R.id.property_name)
        messageTypeView = findViewById(R.id.message_type)
        campaignEnvView = findViewById(R.id.campaign_env)
        accountIdView = findViewById(R.id.account_id)
    }

    fun setMainImageDimensions(width: Int, height: Int) {
//        val lp = mImageView!!.layoutParams
//        lp.width = width
//        lp.height = height
//        mImageView.layoutParams = lp
    }
    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}