package com.sourcepointmeta.metaapp.tv.properties

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.leanback.widget.Presenter
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO

class PropertyViewPresenter(
    private val mContext: Context
) : Presenter() {

    var clickListener: ((view: View, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.property_item, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val propDto = item as? PropertyDTO ?: throw RuntimeException("The item must be a PropertyDTO type!!!")
        val holder = viewHolder.view as? PropertyCardView ?: throw RuntimeException("The view item must be a PropertyCardView type!!!")
        holder.bind(propDto)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) { }
}
