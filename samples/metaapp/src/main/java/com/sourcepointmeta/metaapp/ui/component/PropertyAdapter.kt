package com.sourcepointmeta.metaapp.ui.component

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import kotlinx.android.synthetic.main.property_item.view.*

internal class PropertyAdapter : RecyclerView.Adapter<PropertyAdapter.Vh>() {

    private var list = mutableListOf<PropertyDTO>()
    var itemClickListener: ((PropertyDTO) -> Unit)? = null
    var propertyChangedListener: ((Property) -> Unit)? = null
    var demoProperty: ((property: Property) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.property_item, parent, false)
        return Vh(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position], position)
    }

    class Vh(val view: View) : RecyclerView.ViewHolder(view)

    private fun Vh.bind(iv: PropertyDTO, pos: Int) {
        (view.property_view_item as PropertyItemView).bind(iv)
        view.run {
            val p = list[pos].property
            setOnClickListener { itemClickListener?.invoke(list[pos]) }
            chip_gdpr.setOnCheckedChangeListener { _, isChecked ->
                val editedSet = mutableSetOf<StatusCampaign>().apply {
                    add(StatusCampaign(p.propertyName, CampaignType.GDPR, isChecked))
                    addAll(p.statusCampaignSet)
                }
                play_demo_group.saving = true
                propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
            }
            chip_ccpa.setOnCheckedChangeListener { _, isChecked ->
                val editedSet = mutableSetOf<StatusCampaign>().apply {
                    add(StatusCampaign(p.propertyName, CampaignType.CCPA, isChecked))
                    addAll(p.statusCampaignSet)
                }
                play_demo_group.saving = true
                propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
            }
            chip_usnat.setOnCheckedChangeListener { _, isChecked ->
                val editedSet = mutableSetOf<StatusCampaign>().apply {
                    add(StatusCampaign(p.propertyName, CampaignType.USNAT, isChecked))
                    addAll(p.statusCampaignSet)
                }
                play_demo_group.saving = true
                propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
            }
            play_demo_btn.setOnClickListener {
                demoProperty?.invoke(p)
            }
        }
    }

    fun addItems(newItems: List<PropertyDTO>) {
        list.clear()
        list.addAll(newItems)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getPropertyNameByPosition(position: Int) = list[position].propertyName

    fun isGdprEnabled(propertyName: String): Boolean =
        list.find { it.propertyName == propertyName }?.gdprEnabled == true

    fun savingProperty(propertyName: String, showLoading: Boolean) {
        val position = list.indexOfFirst { it.propertyName == propertyName }
        if (position != -1) {
            list[position].saving = showLoading
            notifyItemChanged(position)
        }
    }

    fun updateProperty(property: PropertyDTO) {
        val position = list.indexOfFirst { it.propertyName == property.propertyName }
        if (position != -1) {
            list[position] = property
            notifyItemChanged(position)
        }
    }
}
