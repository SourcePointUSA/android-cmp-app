package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.readText
import com.sourcepoint.cmplibrary.util.Either
import io.mockk.* // ktlint-disable
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NetworkClientImplTest {

    @MockK
    lateinit var okHttp: OkHttpClient

    @MockK
    private lateinit var responseManager: ResponseManager

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    private val req = MessageReq(
        requestUUID = "test",
        categories = Categories(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview"
            )
        )
    )

//    @Test
    fun `GIVEN a UWReq Object VERIFY the okHttp generated request`() {

        val sut = createNetworkClient(
            httpClient = okHttp,
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )

        sut.getMessage(messageReq = req, success = {}, error = {})

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

//    @Test
    fun `GIVEN a UWReq Object and a real endpoint VERIFY that the output is a Right`() = runBlocking<Unit> {

        val responseManager = ResponseManager.create(JsonConverter.create())

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )

        val res = sut.getMessage(messageReq = req)

        val output = (res as Either.Right<MessageResp>).r
    }

//    @Test
    fun `GIVEN a UWReq Object VERIFY that the output is a Right`() = runBlocking<Unit> {

        every { responseManager.parseResponse(any()) }.returns(Either.Right(mockk()))

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )

        val res = sut.getMessage(messageReq = req)

        val output = (res as Either.Right<MessageResp>).r
    }

//    @Test
    fun `GIVEN an exception VERIFY that the output is a Left`() = runBlocking<Unit> {

        every { responseManager.parseResponse(any()) }.returns(Either.Left(RuntimeException("test")))

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            url = HttpUrlManagerSingleton.inAppLocalUrlMessage
        )

        val res = sut.getMessage(messageReq = req)

        val output = (res as Either.Left).t
        output.message.assertEquals("test")
    }

    private suspend fun NetworkClient.getMessage(messageReq: MessageReq) = suspendCoroutine<Either<MessageResp>> {
        getMessage(
            messageReq,
            { messageResp -> it.resume(Either.Right(messageResp)) },
            { throwable -> it.resume(Either.Left(throwable)) }
        )
    }
}
