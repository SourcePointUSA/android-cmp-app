package com.sourcepointmeta.metaapp.tv.properties

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO

class PropertyCardView : BaseCardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}

fun PropertyCardView.bind(item: PropertyDTO) {
    val campaignEnv = findViewById<TextView>(R.id.campaign_env)
    val propertyName = findViewById<TextView>(R.id.property_name)
    val accountId = findViewById<TextView>(R.id.account_id)
    val gdprIcon = findViewById<ImageView>(R.id.gdpr_icon)
    val ccpaIcon = findViewById<ImageView>(R.id.ccpa_icon)
    val usNatIcon = findViewById<ImageView>(R.id.usnat_icon)

    campaignEnv.text = item.campaignEnv
    propertyName.text = item.propertyName
    accountId.text = "${item.accountId}"
    gdprIcon.setImageResource(getResStatusResIcon(item.gdprEnabled))
    ccpaIcon.setImageResource(getResStatusResIcon(item.ccpaEnabled))
    usNatIcon.setImageResource(getResStatusResIcon(item.usnatEnabled))
}

fun getResStatusResIcon(enable: Boolean) = when (enable) {
    true -> R.drawable.ic_baseline_check_24
    false -> R.drawable.ic_baseline_close_24
}
