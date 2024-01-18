package com.sourcepointmeta.metaapp.ui.component

import android.app.AlertDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import kotlinx.android.synthetic.main.item_action_demo.view.* // ktlint-disable
import kotlinx.android.synthetic.main.property_item.view.* // ktlint-disable

fun PropertyItemView.bind(
    item: PropertyDTO
) {

    chip_gdpr.setOnCheckedChangeListener(null)
    chip_ccpa.setOnCheckedChangeListener(null)
    chip_usnat.setOnCheckedChangeListener(null)

    campaign_env.text = item.campaignEnv
    property_name.text = item.propertyName
    account_id.text = "${item.accountId}"
    chip_gdpr.isChecked = item.gdprEnabled
    chip_ccpa.isChecked = item.ccpaEnabled
    chip_usnat.isChecked = item.usnatEnabled
    play_demo_group.saving = item.saving
}

fun DemoActionItemView.bind(item: DemoActionItem) {
    action_item.text = item.message
}

fun ChipGroup.addChip(content: String) {
    val chip = Chip(context, null, R.style.Widget_MaterialComponents_Chip_Choice)
    chip.text = content
    chip.isCloseIconVisible = true
    chip.setOnCloseIconClickListener { c ->
        AlertDialog.Builder(context)
            .setTitle("Deleting [${(c as Chip).text}]")
            .setMessage("Click on delete button to confirm")
            .setPositiveButton("Delete") { _, _ -> removeView(c) }
            .setNegativeButton("Cancel", null)
            .show()
    }
    addView(chip)
}

fun MetaLog.toLogItem() = LogItem(
    message = message,
    status = statusReq,
    type = type,
    timestamp = timestamp,
    propertyName = propertyName,
    id = id,
    tag = tag,
    jsonBody = jsonBody
)
