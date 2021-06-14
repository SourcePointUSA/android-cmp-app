package com.sourcepointmeta.metaapp.ui.component

import android.app.AlertDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sourcepointmeta.metaapp.R
import kotlinx.android.synthetic.main.add_property_fragment.view.*
import kotlinx.android.synthetic.main.property_item.view.*
import kotlinx.android.synthetic.main.property_item.view.chip_ccpa
import kotlinx.android.synthetic.main.property_item.view.chip_gdpr

fun PropertyItemView.bind(
    item: PropertyDTO
) {

    chip_gdpr.setOnCheckedChangeListener(null)
    chip_ccpa.setOnCheckedChangeListener(null)

    campaign_env.text = item.campaignEnv
    property_name.text = item.propertyName
    account_id.text = "${item.accountId}"
    message_type.text = item.messageType
    chip_gdpr.isChecked = item.gdprEnabled
    chip_ccpa.isChecked = item.ccpaEnabled
    play_demo_group.saving = item.saving
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
