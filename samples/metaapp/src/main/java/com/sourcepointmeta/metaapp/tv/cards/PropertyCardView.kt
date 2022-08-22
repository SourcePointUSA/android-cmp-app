package com.sourcepointmeta.metaapp.tv.cards

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.google.android.material.chip.Chip
import com.sourcepointmeta.metaapp.R

open class PropertyCardView(context: Context?) : BaseCardView(context) {
    var propertyNameView: TextView? = null
    var messageTypeView: TextView? = null
    var campaignEnvView: TextView? = null
    var accountIdView: TextView? = null
    var chipGDPR: Chip? = null
    var chipCCPA: Chip? = null

    init {
        val inflater = LayoutInflater.from(getContext())
        inflater.inflate(R.layout.property_item, this)
        propertyNameView = findViewById(R.id.property_name)
        messageTypeView = findViewById(R.id.message_type)
        campaignEnvView = findViewById(R.id.campaign_env)
        accountIdView = findViewById(R.id.account_id)
        chipGDPR = findViewById(R.id.chip_gdpr)
        chipCCPA = findViewById(R.id.chip_ccpa)
    }

    fun setGDPR(status: Boolean) { chipGDPR?.let { it.isChecked = status } }
    fun setCCPA(status: Boolean) { chipCCPA?.let { it.isChecked = status } }

    override fun hasOverlappingRendering(): Boolean {
        return false
    }
}
