package com.sourcepoint.gdpr_cmplibrary.data.network

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

fun Call.enqueue(block: OkHttpCallbackImpl.() -> Unit) {
    enqueue(OkHttpCallbackImpl().apply(block))
}

class OkHttpCallbackImpl : Callback {

    private var onFailure_: ((call: Call, exception: IOException) -> Unit)? = null
    private var onResponse_: ((call: Call, r: Response) -> Unit)? = null

    fun onFailure(init: ((call: Call, exception: IOException) -> Unit)) {
        onFailure_ = init
    }

    fun onResponse(init: ((call: Call, r: Response) -> Unit)) {
        onResponse_ = init
    }

    override fun onFailure(call: Call, e: IOException) {
        onFailure_?.invoke(call, e)
    }

    override fun onResponse(call: Call, r: Response) {
        onResponse_?.invoke(call, r)
    }
}