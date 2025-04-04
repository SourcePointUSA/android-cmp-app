package com.sourcepointmeta.metaapp.logger

import android.util.Log
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.* // ktlint-disable

@OptIn(DelicateCoroutinesApi::class)
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

    override fun webAppAction(tag: String, msg: String, json: JSONObject?) {
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

    override fun nativeMessageAction(tag: String, msg: String, json: JSONObject?) {
        loggerScope.launch {
            ds.storeOrUpdateLog(
                MetaLog(
                    id = null,
                    propertyName = propertyName,
                    timestamp = Date().time,
                    type = "NATIVEMESSAGE_ACTION",
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

    override fun computation(tag: String, msg: String, json: JSONObject?) {
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
                    jsonBody = json.toString()
                )
            )
        }
    }

    override fun clientEvent(event: String, msg: String, content: String) {
        loggerScope.launch {
            if (event == "log" && !content.contains("sp.renderingAppError")) {
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
            } else {
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
    }

    override fun pm(tag: String, url: String, type: String, params: String?) {
        println("$tag - $url")
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
                    jsonBody = params
                )
            )
        }
    }

    override fun flm(tag: String, url: String, type: String, json: JSONObject) {
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
                    jsonBody = json.toString()
                )
            )
        }
    }
}
