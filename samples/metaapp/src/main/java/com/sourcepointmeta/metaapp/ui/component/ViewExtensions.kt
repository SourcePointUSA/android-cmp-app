package com.sourcepointmeta.metaapp.ui.component

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.View
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import kotlinx.android.synthetic.main.item_action_demo.view.*
import kotlinx.android.synthetic.main.item_log.view.*
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

fun DemoActionItemView.bind(item: DemoActionItem) {
    action_item.text = item.message
}

@SuppressLint("ResourceType")
fun LogItemView.bind(item: LogItem, position: Int) {
    when (item.type) {
        "REQUEST" -> bindReq(item, position)
        "RESPONSE" -> bindResp(item, position)
        "WEB_ACTION" -> bindWebAction(item, position)
        "ERROR" -> bindClientError(item, position)
        "CLIENT_EVENT" -> bindClientEvent(item, position)
        "COMPUTATION" -> bindComputation(item, position)
        else -> throw RuntimeException("No type found!!!")
    }
}

fun LogItemView.bindReq(item: LogItem, position: Int) {
    val url = item.message
    log_title.text = "${item.type} - ${item.tag}"
    log_body.text = url
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun LogItemView.bindResp(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.text = "Status: ${item.status}"
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun LogItemView.bindWebAction(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.text = item.message
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun LogItemView.bindComputation(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.visibility = View.GONE
    log_body_1.text = item.message
}

fun LogItemView.bindClientEvent(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.text = item.message
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun LogItemView.bindClientError(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.visibility = View.GONE
    log_body_1.text = if (item.message.length > 200) item.message.subSequence(0, 200) else item.message
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
