package com.sourcepoint.gdpr_cmplibrary

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

fun Call.enqueue(block : OkHttpCallbackImpl.() -> Unit){
    enqueue(OkHttpCallbackImpl().apply(block))
}

class OkHttpCallbackImpl : Callback{
    
    private var onFailure_ : ((call: Call, exception: IOException) -> Unit)? = null
    private var onResponse_ : ((call: Call, r: Response) -> Unit)? = null
    
    fun onFailure(init : ((call: Call, exception: IOException) -> Unit)) {
        onFailure_ = init
    }

    fun onResponse(init : ((call: Call, r: Response) -> Unit)) {
        onResponse_ = init
    }
    
    override fun onFailure(call: Call, e: IOException) {
        Log.d("ELogger", e.stackTraceToString())
        onFailure_?.invoke(call, e)
    }

    override fun onResponse(call: Call, r: Response) {
        Log.d("ELogger", r.toString())
        onResponse_?.invoke(call, r)
    }
}