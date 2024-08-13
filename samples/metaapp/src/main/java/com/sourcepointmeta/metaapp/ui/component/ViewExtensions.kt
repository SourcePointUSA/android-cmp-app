package com.sourcepointmeta.metaapp.ui.component

import android.app.AlertDialog
import android.widget.TextView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import com.sourcepointmeta.metaapp.databinding.PropertyItemBinding

fun PropertyItemView.bind(
    item: PropertyDTO,
    binding: PropertyItemBinding
) {

    binding.chipGdpr?.setOnCheckedChangeListener(null)
    binding.chipCcpa?.setOnCheckedChangeListener(null)
    binding.chipUsnat?.setOnCheckedChangeListener(null)

    binding.campaignEnv.text = item.campaignEnv
    binding.propertyName.text = item.propertyName
    binding.accountId.text = "${item.accountId}"
    binding.chipGdpr?.isChecked = item.gdprEnabled
    binding.chipCcpa?.isChecked = item.ccpaEnabled
    binding.chipUsnat?.isChecked = item.usnatEnabled
    binding.playDemoGroup?.saving = item.saving
}

fun DemoActionItemView.bind(item: DemoActionItem) {
    findViewById<TextView>(R.id.action_item).text = item.message
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
