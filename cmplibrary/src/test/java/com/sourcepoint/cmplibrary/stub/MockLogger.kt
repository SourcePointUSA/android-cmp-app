package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.exception.Logger
import org.json.JSONObject

object MockLogger : Logger {
    override fun error(e: RuntimeException) { }
    override fun e(tag: String, msg: String) { }
    override fun i(tag: String, msg: String) { }
    override fun d(tag: String, msg: String) { }
    override fun v(tag: String, msg: String) { }
    override fun req(tag: String, url: String, type: String, body: String) { }
    override fun res(tag: String, msg: String, status: String, body: String) { }
    override fun webAppAction(tag: String, msg: String, json: JSONObject?) { }
    override fun nativeMessageAction(tag: String, msg: String, json: JSONObject?) { }
    override fun computation(tag: String, msg: String) { }
    override fun computation(tag: String, msg: String, json: JSONObject?) { }
    override fun clientEvent(event: String, msg: String, content: String) { }
    override fun pm(tag: String, url: String, type: String, params: String?) { }
    override fun flm(tag: String, url: String, type: String, json: JSONObject) { }
}
