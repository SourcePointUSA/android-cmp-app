package com.sourcepoint.cmplibrary.stub

import io.mockk.mockk
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.Timeout

class MockCall(private val logicResponseCB: (Callback) -> Unit) : Call {
    override fun enqueue(responseCallback: Callback) {
        logicResponseCB(responseCallback)
    }
    override fun execute(): Response = mockk()
    override fun isCanceled(): Boolean = false
    override fun isExecuted(): Boolean = true
    override fun request(): Request = mockk()
    override fun timeout(): Timeout = mockk()
    override fun cancel() {}
    override fun clone(): Call = this
}
