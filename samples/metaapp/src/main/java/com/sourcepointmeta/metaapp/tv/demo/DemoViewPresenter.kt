package com.sourcepointmeta.metaapp.tv.demo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.LogItem

class DemoViewPresenter(
    private val mContext: Context
) : Presenter() {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.log_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val propDto = item as? LogItem ?: throw RuntimeException("The item must be a PropertyDTO type!!!")
        val holder = viewHolder.view as? DemoLogView ?: throw RuntimeException("The view item must be a PropertyCardView type!!!")
        holder.bind(propDto)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) { }
}
