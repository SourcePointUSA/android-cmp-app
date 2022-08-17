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
 * A PropertyCardPresenter is used to generate Views and bind Objects to them on demand.
 */
class PropertyCardPresenter(
    val context: Context
) : Presenter() {
    companion object {
        private val TAG = "PropertyCardPresenter"
    }

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        Log.d(TAG, "onCreateViewHolder")

        val cardView = object : PropertyCardView(parent.context) {}

        cardView.isFocusable = true
        cardView.isClickable = true
        return Presenter.ViewHolder(cardView)
    }
    @SuppressLint("ResourceType")
    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        val property = item as PropertyDTO
        val cardView = viewHolder.view as PropertyCardView

        Log.d(TAG, "onBindViewHolder")
        cardView.propertyNameView?.let{ it.text = property.propertyName }
        cardView.messageTypeView?.let{ it.text = property.messageType }
        cardView.accountIdView?.let{ it.text = property.accountId.toString() }
        cardView.campaignEnvView?.let{ it.text = property.campaignEnv }
        cardView.chipGDPR?.let{ it.isChecked = property.gdprEnabled }
        cardView.chipCCPA?.let{ it.isChecked = property.ccpaEnabled }
    }
    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
    }
}