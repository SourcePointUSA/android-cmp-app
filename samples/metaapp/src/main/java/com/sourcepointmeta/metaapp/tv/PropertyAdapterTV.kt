package com.sourcepointmeta.metaapp.tv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO

internal class PropertyAdapterTV() : BaseAdapter() {

    private var list = mutableListOf<PropertyDTO>()
    var itemClickListener: ((PropertyDTO) -> Unit)? = null
    var propertyChangedListener: ((Property) -> Unit)? = null
    var demoProperty: ((property: Property) -> Unit)? = null

    fun isGdprEnabled(propertyName: String): Boolean =
            list.find { it.propertyName == propertyName }?.gdprEnabled == true

    fun addItems(newItems: List<PropertyDTO>) {
        list.clear()
        list.addAll(newItems)
        this.notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        list.removeAt(position)
        this.notifyDataSetChanged()
    }

    fun savingProperty(propertyName: String, showLoading: Boolean) {
        val position = list.indexOfFirst { it.propertyName == propertyName }
        if (position != -1) {
            list[position].saving = showLoading
            this.notifyDataSetChanged()
        }
    }

    fun updateProperty(property: PropertyDTO) {
        val position = list.indexOfFirst { it.propertyName == property.propertyName }
        if (position != -1) {
            list[position] = property
            this.notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    fun SetChipStyle(chip: Chip, isEnabled: Boolean){
        chip.isChecked = isEnabled
        if(isEnabled)
        {
            chip.setChipBackgroundColorResource(R.color.green_status_200)
        }
        else {
            chip.setChipBackgroundColorResource(R.color.lb_grey)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if (convertView == null) {
            val convertView = LayoutInflater.from(parent?.context)
                .inflate(R.layout.property_item, parent, false)

            val accountId = convertView?.findViewById<TextView>(R.id.account_id)
            accountId?.text = list[position].accountId.toString()

            val propertyName = convertView?.findViewById<TextView>(R.id.property_name)
            propertyName?.text = list[position].propertyName

            val messageType = convertView?.findViewById<TextView>(R.id.message_type)
            messageType?.text = list[position].messageType

            val campaignEnv = convertView?.findViewById<TextView>(R.id.campaign_env)
            campaignEnv?.text = list[position].campaignEnv

            val chip_gdpr = convertView?.findViewById<Chip>(R.id.chip_gdpr)
            if (chip_gdpr != null) {
                SetChipStyle(chip_gdpr, list[position].gdprEnabled)
            }
            val chip_ccpa = convertView?.findViewById<Chip>(R.id.chip_ccpa)
            if (chip_ccpa != null) {
                SetChipStyle(chip_ccpa, list[position].ccpaEnabled)
            }

            val p = list[position].property
            chip_gdpr?.setOnCheckedChangeListener { _, isChecked ->
                val editedSet = mutableSetOf<StatusCampaign>().apply {
                    add(StatusCampaign(p.propertyName, CampaignType.GDPR, isChecked))
                    addAll(p.statusCampaignSet)
                }
//                    play_demo_group.saving = true
                propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
            }
            chip_ccpa?.setOnCheckedChangeListener { _, isChecked ->
                val editedSet = mutableSetOf<StatusCampaign>().apply {
                    add(StatusCampaign(p.propertyName, CampaignType.CCPA, isChecked))
                    addAll(p.statusCampaignSet)
                }
//                    play_demo_group.saving = true
                propertyChangedListener?.invoke(p.copy(statusCampaignSet = editedSet))
            }

//            (convertView.property_view_item as PropertyItemView).bind(iv)
//            view.run {
//                val p = list[pos].property
//                setOnClickListener { itemClickListener?.invoke(list[pos]) }
//
//                play_demo_btn.setOnClickListener {
//                    demoProperty?.invoke(p)
//                }
//        }
            return convertView
        }
        return convertView
    }

    override fun getItemViewType(position: Int): Int {
        return IGNORE_ITEM_VIEW_TYPE
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEnabled(position: Int): Boolean {
        return true
    }

//    override fun onBindViewHolder(holder: PropertyAdapter.Vh, position: Int) {
//        holder.bind(list[position], position)
//    }

}