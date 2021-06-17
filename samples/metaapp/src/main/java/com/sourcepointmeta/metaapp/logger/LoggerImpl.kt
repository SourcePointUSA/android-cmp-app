package com.sourcepointmeta.metaapp.logger

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.* // ktlint-disable
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.* // ktlint-disable

internal class LoggerImpl(
    private val propertyName: String,
    private val ds: LocalDataSource,
    private val session: String
) : Logger {

    private val loggerScope = GlobalScope

    override fun error(e: RuntimeException) {
        e.printStackTrace()
    }

    override fun e(tag: String, msg: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "ERROR",
                    tag = tag,
                    message = msg,
                    logSession = session
                )
            )
        }
        Log.e(tag, msg)
    }

    override fun i(tag: String, msg: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "INFO",
                    tag = tag,
                    message = msg,
                    logSession = session
                )
            )
        }
        Log.i(tag, msg)
    }

    override fun d(tag: String, msg: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "DEBUG",
                    tag = tag,
                    message = msg,
                    logSession = session
                )
            )
        }
        Log.d(tag, msg)
    }

    override fun v(tag: String, msg: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "VERBOSE",
                    tag = tag,
                    message = msg,
                    logSession = session
                )
            )
        }
        Log.v(tag, msg)
    }

    override fun req(tag: String, url: String, type: String, body: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "REQUEST",
                    tag = tag,
                    message = url,
                    logSession = session,
                    jsonBody = body
                )
            )
        }
    }

    override fun res(tag: String, msg: String, status: String, body: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "RESPONSE",
                    tag = tag,
                    message = msg,
                    logSession = session,
                    statusReq = status,
                    jsonBody = body
                )
            )
        }
    }

    override fun actionWebApp(tag: String, msg: String, json: JSONObject?) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "WEB_ACTION",
                    tag = tag,
                    message = msg,
                    logSession = session,
                    jsonBody = json.toString()
                )
            )
        }
    }

    override fun computation(tag: String, msg: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "COMPUTATION",
                    tag = tag,
                    message = msg,
                    logSession = session,
                    jsonBody = null
                )
            )
        }
    }

    override fun clientEvent(event: String, msg: String, content: String) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "CLIENT_EVENT",
                    tag = event,
                    message = msg,
                    logSession = session,
                    jsonBody = content
                )
            )
        }
    }

    override fun pm(tag: String, url: String, type: String, pmId: String?) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "REQUEST",
                    tag = tag,
                    message = url,
                    logSession = session,
                    jsonBody = pmId
                )
            )
        }
    }
}

fun TextView.setColorOfSubstring(substring: String, color: Int) {
    try {
        val spannable = android.text.SpannableString(text)
        val start = text.indexOf(substring)
        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            start,
            start + substring.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = spannable
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun SpannableStringBuilder.spanText(span: Any): SpannableStringBuilder {
    setSpan(span, 0, length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}

private fun String.toSpannable() = SpannableStringBuilder(this)

fun String.foregroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = ForegroundColorSpan(color)
    return toSpannable().spanText(span)
}

fun String.backgroundColor(@ColorInt color: Int): SpannableStringBuilder {
    val span = BackgroundColorSpan(color)
    return toSpannable().spanText(span)
}

fun String.relativeSize(size: Float): SpannableStringBuilder {
    val span = RelativeSizeSpan(size)
    return toSpannable().spanText(span)
}

fun String.supserscript(): SpannableStringBuilder {
    val span = SuperscriptSpan()
    return toSpannable().spanText(span)
}

fun String.strike(): SpannableStringBuilder {
    val span = StrikethroughSpan()
    return toSpannable().spanText(span)
}
