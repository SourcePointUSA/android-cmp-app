package com.sourcepointmeta.metaapp.tv.cards

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.BaseCardView
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import kotlin.properties.Delegates

/**
 * A CardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an ImageCardView.
 */
class CardPresenter(
    val context: Context
) : Presenter() {
    companion object {
        private val TAG = "CardPresenter"

//        private val CARD_WIDTH = 200
//        private val CARD_HEIGHT = 100
        private val CARD_WIDTH = 313
        private val CARD_HEIGHT = 176
    }

    private var mDefaultCardImage: Drawable? = null
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        Log.d(TAG, "onCreateViewHolder")

        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.cardview_light_background)
        sSelectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.cardview_dark_background)
        mDefaultCardImage = ContextCompat.getDrawable(parent.context, R.drawable.ic_baseline_ios_share_24)

        val cardView = object : TextCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        updateCardBackgroundColor(cardView, false)
        return Presenter.ViewHolder(cardView)
    }
    @SuppressLint("ResourceType")
    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val property = item as PropertyDTO
        val cardView = viewHolder.view as TextCardView

        Log.d(TAG, "onBindViewHolder")
        cardView.propertyNameView!!.text = property.propertyName
        cardView.messageTypeView!!.text = property.messageType
        cardView.accountIdView!!.text = property.authId
        cardView.campaignEnvView!!.text = property.campaignEnv
        cardView.chipGDPR!!.isChecked = property.gdprEnabled
        cardView.chipCCPA!!.isChecked = property.ccpaEnabled
    }
    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
    }

    private fun updateCardBackgroundColor(view: BaseCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view"s background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
//        view.setInfoAreaBackgroundColor(color)
    }

}