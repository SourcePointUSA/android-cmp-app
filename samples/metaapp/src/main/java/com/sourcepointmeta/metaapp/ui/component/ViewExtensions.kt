package com.sourcepointmeta.metaapp.ui.component

import kotlinx.android.synthetic.main.property_item.view.*

fun PropertyItemView.bind(
    item: PropertyDTO
) {
    campaign_env.text = item.campaignEnv
    property_name.text = item.propertyName
    account_id.text = "${item.accountId}"
    message_type.text = item.messageType
}
