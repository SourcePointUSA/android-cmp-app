package com.sourcepointmeta.metaapp.tv.cards

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO

class TextCardPresenter(
    val context: Context
) : Presenter() {
    companion object {
        private val TAG = "TextCardPresenter"

        private val CARD_WIDTH = 200
        private val CARD_HEIGHT = 155
    }

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
        Log.d(TAG, "onCreateViewHolder")

        val cardView = object : TextCardView(parent.context) {}

        cardView.isFocusable = true
        cardView.isClickable = true
        return Presenter.ViewHolder(cardView)
    }
    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
        if(item is String) {
            val text = item as String
            val cardView = viewHolder.view as TextCardView

            Log.d(TAG, "onBindViewHolder")
            cardView.textView!!.text = text
        }
    }
    override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {
        Log.d(TAG, "onUnbindViewHolder")
    }
}