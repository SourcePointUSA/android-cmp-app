package com.sourcepointmeta.metaapp.ui.eventlogs

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.ui.component.LogItem
import com.sourcepointmeta.metaapp.ui.component.LogItemView
import kotlinx.android.synthetic.main.item_log.view.* // ktlint-disable
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject

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
    setWebLink(url, log_body)
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun LogItemView.bindResp(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    setStatusField(item.status ?: "", log_body)
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun LogItemView.setStatusField(status: String, textView: TextView) {
    textView.also {
        it.text = "Status: $status"
        when (status.toIntOrNull()) {
            in (200..299) -> it.setTextColor(responseSuccessColor)
            in (300..599) -> it.setTextColor(responseErrorColor)
            else -> it.setTextColor(colorTextOnSurface)
        }
    }
}

fun LogItemView.setWebLink(link: String, textView: TextView) {
    textView.also {
        it.text = link
        when (link.toHttpUrlOrNull()) {
            null -> it.setTextColor(colorTextOnSurface)
            else -> it.setTextColor(colorLink)
        }
    }
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
    val errorObject =
        item.jsonBody?.let { com.sourcepointmeta.metaapp.util.check { JSONObject(it) }.getOrNull() } ?: JSONObject()
    val title: String? = errorObject.getOrNull("title")
    val stackTrace: String = errorObject.getOrNull("stackTrace") ?: ""
    log_body.setTextColor(colorClientEvent)
    when (title) {
        null -> log_body.text = item.message
        else -> log_body.text = "$title - $stackTrace"
    }
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun JSONObject.getOrNull(key: String): String? = if (has(key)) this.getString(key) else null

fun LogItemView.bindClientError(item: LogItem, position: Int) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.visibility = View.GONE
    log_body_1.text = if (item.message.length > 200) item.message.subSequence(0, 200) else item.message
}
