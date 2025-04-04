package com.sourcepointmeta.metaapp.ui.component

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.databinding.PropertyItemBinding

internal class PropertyAdapter : RecyclerView.Adapter<PropertyAdapter.Vh>() {

    private var list = mutableListOf<PropertyDTO>()
    var itemClickListener: ((PropertyDTO) -> Unit)? = null
    var propertyChangedListener: ((Property) -> Unit)? = null
    var demoProperty: ((property: Property) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        val binding = PropertyItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return Vh(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.bind(list[position], position)
    }

    class Vh(val binding: PropertyItemBinding) : RecyclerView.ViewHolder(binding.root)

    private fun Vh.bind(iv: PropertyDTO, pos: Int) {
        (binding.propertyViewItem as PropertyItemView).bind(iv, binding)

        val p = list[pos].property
        binding.root.setOnClickListener { itemClickListener?.invoke(list[pos]) }

        binding.chipGdpr?.setOnCheckedChangeListener { _, isChecked ->
            val editedSet = mutableSetOf<StatusCampaign>().apply {
                add(StatusCampaign(p.propertyName, CampaignType.GDPR, isChecked))
                addAll(p.statusCampaignSet)
            }
            binding.playDemoGroup?.saving = true
            propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
        }
        binding.chipCcpa?.setOnCheckedChangeListener { _, isChecked ->
            val editedSet = mutableSetOf<StatusCampaign>().apply {
                add(StatusCampaign(p.propertyName, CampaignType.CCPA, isChecked))
                addAll(p.statusCampaignSet)
            }
            binding.playDemoGroup?.saving = true
            propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
        }
        binding.chipUsnat?.setOnCheckedChangeListener { _, isChecked ->
            val editedSet = mutableSetOf<StatusCampaign>().apply {
                add(StatusCampaign(p.propertyName, CampaignType.USNAT, isChecked))
                addAll(p.statusCampaignSet)
            }
            binding.playDemoGroup?.saving = true
            propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
        }
        binding.playDemoBtn?.setOnClickListener {
            demoProperty?.invoke(p)
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
