package com.sourcepoint.gdpr_cmplibrary

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.Timeout
import org.junit.Before
import org.junit.Test
import java.io.IOException

class OkHttpCallbackImplTest {

    @MockK
    private lateinit var mockCall: Call

    @MockK
    private lateinit var mockResponse: Response

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `SETUP the Call extension check the onResponse callback`() {

        val stub = StubCallObj(mockCall, mockResponse)

        var callFailure: Call? = null
        var exception: IOException? = null
        var callResp: Call? = null
        var resp: Response? = null

        stub.enqueue {
            onResponse { call, r ->
                callResp = call
                resp = r
            }
            onFailure { call, e ->
                callFailure = call
                exception = e
            }
        }

        stub.triggerOnResponse()

        callFailure.assertNull()
        exception.assertNull()
        callResp.assertNotNull()
        resp.assertNotNull()

    }

    @Test
    fun `SETUP the Call extension check the onFailure callback`() {

        val stub = StubCallObj(mockCall, mockResponse)

        var callFailure: Call? = null
        var exception: IOException? = null
        var callResp: Call? = null
        var resp: Response? = null

        stub.enqueue {
            onResponse { call, r ->
                callResp = call
                resp = r
            }
            onFailure { call, e ->
                callFailure = call
                exception = e
            }
        }

        stub.triggerOnFailure()

        callFailure.assertNotNull()
        exception.assertNotNull()
        callResp.assertNull()
        resp.assertNull()

    }

    private class StubCallObj(val call: Call, val response: Response) : Call {

        private lateinit var respCallback: Callback

        override fun cancel() {}
        override fun clone(): Call {
            TODO("Not yet implemented")
        }

        override fun enqueue(responseCallback: Callback) {
            respCallback = responseCallback
        }

        override fun execute(): Response = Response.Builder().build()
        override fun isCanceled(): Boolean = false
        override fun isExecuted(): Boolean = true
        override fun request(): Request = Request.Builder().build()
        override fun timeout(): Timeout = Timeout.NONE

        fun triggerOnResponse() = respCallback.onResponse(call, response)
        fun triggerOnFailure() = respCallback.onFailure(call, IOException())
    }


}