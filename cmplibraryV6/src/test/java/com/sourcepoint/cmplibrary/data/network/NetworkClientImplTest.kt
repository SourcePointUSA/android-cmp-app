package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.readText
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import org.junit.Test

class NetworkClientImplTest {

    @MockK
    lateinit var okHttp: OkHttpClient

    @MockK
    lateinit var success: (UWResp) -> Unit

    @MockK
    lateinit var error: (Throwable) -> Unit

    @MockK
    internal lateinit var responseManager: ResponseManager

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    val req = UWReq(
        requestUUID = "test",
        categories = Categories(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview"
            )
        )
    )

    @Test
    fun `GIVEN a UWReq Object VERIFY the okHttp generated request`() {

        val sut =  createNetworkClient(
            httpClient = okHttp,
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )

        sut.getMessage(uwReq = req, success = success, error = error)

        val slot = slot<Request>()
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }

        slot.captured.run {
            readText().assertEquals(req.toBodyRequest())
            url.toString().assertEquals("http://localhost:3000/wrapper/v1/unified/message?env=localProd&inApp=true")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals("localProd")
            url.queryParameter("inApp").assertEquals("true")
        }
    }

    @Test
    fun `GIVEN a UWReq Object and a real endpoint VERIFY the okHttp response`() = runBlocking<Unit> {

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )
        sut.getMessage(uwReq = req, success = success, error = error)

        verify(exactly = 0) { success(any()) }
        verify(exactly = 0) { error(any()) }

        delay(5000)

    }

    @Test
    fun `GIVEN a UWReq Object and a real endpoint VERIFY the okHttp response susp`() = runBlocking<Unit> {

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )

        val res = sut.getMessage(uwReq = req)

        verify(exactly = 0) { success(any()) }
        verify(exactly = 0) { error(any()) }

    }

}