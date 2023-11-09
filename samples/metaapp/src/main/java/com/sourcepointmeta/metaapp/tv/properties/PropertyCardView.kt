package com.sourcepointmeta.metaapp.tv.properties

import android.content.Context
import android.util.AttributeSet
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import kotlinx.android.synthetic.main.property_item.view.* //ktlint-disable

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
    campaign_env.text = item.campaignEnv
    property_name.text = item.propertyName
    account_id.text = "${item.accountId}"
    gdpr_icon.setImageResource(getResStatusResIcon(item.gdprEnabled))
    ccpa_icon.setImageResource(getResStatusResIcon(item.ccpaEnabled))
    ccpa_icon.setImageResource(getResStatusResIcon(item.usnatEnabled))
}

fun getResStatusResIcon(enable: Boolean) = when (enable) {
    true -> R.drawable.ic_baseline_check_24
    false -> R.drawable.ic_baseline_close_24
}
