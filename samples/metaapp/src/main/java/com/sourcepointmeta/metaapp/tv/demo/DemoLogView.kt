package com.sourcepointmeta.metaapp.tv.demo

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.leanback.widget.BaseCardView
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.ui.component.LogItem
import com.sourcepointmeta.metaapp.ui.eventlogs.*
import kotlinx.android.synthetic.main.item_log.view.*
import kotlinx.android.synthetic.main.log_item.view.*
import kotlinx.android.synthetic.main.log_item.view.log_body
import kotlinx.android.synthetic.main.log_item.view.log_body_1
import kotlinx.android.synthetic.main.log_item.view.log_title
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject

class DemoLogView : BaseCardView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    val responseSuccessColor: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorSuccessResponse, this, true) }
            .data
    }

    val responseErrorColor: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorErrorResponse, this, true) }
            .data
    }

    val colorLink: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.webLink, this, true) }
            .data
    }

    val colorTextOnSurface: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorOnSurface, this, true) }
            .data
    }

    val colorClientEvent: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorClientEvent, this, true) }
            .data
    }

    val colorWebAction: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorWebAction, this, true) }
            .data
    }

    val colorComputation: Int by lazy {
        TypedValue().apply { context.theme.resolveAttribute(R.attr.colorItemComputation, this, true) }
            .data
    }
}

fun DemoLogView.bind(item: LogItem) {
    when (item.type) {
        "REQUEST" -> bindReq(item)
        "RESPONSE" -> bindResp(item)
        "WEB_ACTION" -> bindWebAction(item)
        "NATIVEMESSAGE_ACTION" -> bindWebAction(item)
        "ERROR" -> bindClientError(item)
        "CLIENT_EVENT" -> bindClientEvent(item)
        "COMPUTATION" -> bindComputation(item)
        else -> throw RuntimeException("No type found!!!")
    }
}

fun DemoLogView.bindReq(item: LogItem) {
    val url = item.message
    log_title.text = "${item.type} - ${item.tag}"
    setWebLink(url, log_body)
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun DemoLogView.bindResp(item: LogItem) {
    log_title.text = "${item.type} - ${item.tag}"
    setStatusField(item.status ?: "", log_body)
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun DemoLogView.setStatusField(status: String, textView: TextView) {
    textView.also {
        it.text = "Status: $status"
        when (status.toIntOrNull()) {
            in (200..299) -> it.setTextColor(responseSuccessColor)
            in (300..599) -> it.setTextColor(responseErrorColor)
            else -> it.setTextColor(colorTextOnSurface)
        }
    }
}

fun DemoLogView.setWebLink(link: String, textView: TextView) {
    textView.also {
        it.text = link
        when (link.toHttpUrlOrNull()) {
            null -> it.setTextColor(colorTextOnSurface)
            else -> it.setTextColor(colorLink)
        }
    }
}

fun DemoLogView.bindWebAction(item: LogItem) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.text = item.message
    log_body.setTextColor(colorWebAction)
    item.jsonBody
        ?.let { log_body_1.text = if (it.length > 200) it.subSequence(0, 200) else it }
        ?: run { log_body_1.visibility = View.GONE }
}

fun DemoLogView.bindComputation(item: LogItem) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.visibility = View.GONE
    log_body_1.text = item.message
}

fun DemoLogView.bindClientEvent(item: LogItem) {
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

fun DemoLogView.bindClientError(item: LogItem) {
    log_title.text = "${item.type} - ${item.tag}"
    log_body.visibility = View.GONE
    log_body_1.text = if (item.message.length > 200) item.message.subSequence(0, 200) else item.message
}
