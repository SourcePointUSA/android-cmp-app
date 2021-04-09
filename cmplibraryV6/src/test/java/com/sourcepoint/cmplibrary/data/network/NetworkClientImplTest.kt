package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.create
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.readText
import com.sourcepoint.cmplibrary.stub.MockCall
import io.mockk.* // ktlint-disable
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Before
import org.junit.Test
import java.lang.RuntimeException
import kotlin.coroutines.suspendCoroutine

class NetworkClientImplTest {

    @MockK
    lateinit var okHttp: OkHttpClient

    @MockK
    private lateinit var successMock: (UnifiedMessageResp) -> Unit

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

    @MockK
    private lateinit var responseManager: ResponseManager

    @MockK
    private lateinit var logger: Logger

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    private val req = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                accountId = 22,
                propertyHref = "https://tcfv2.mobile.webview",
                targetingParams = TargetingParams(
                    legislation = Legislation.GDPR.name,
                    location = "EU"
                ).toJsonObjStringify()
            ),
            ccpa = CcpaReq(
                accountId = 22,
                propertyHref = "https://tcfv2.mobile.webview",
                targetingParams = TargetingParams(
                    legislation = Legislation.CCPA.name,
                    location = "US"
                ).toJsonObjStringify()
            )
        )
    )

    private val sut by lazy {
        createNetworkClient(
            httpClient = okHttp,
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = logger
        )
    }

    @Test
    fun `GIVEN a UWReq Object VERIFY the okHttp generated request`() {
        /** execution */
//        sut.getMessage(messageReq = req, pSuccess = { successMock(it) }, pError = { errorMock(it) }, stage = Env.STAGE)

        val slot = slot<Request>()
//        verify(exactly = 1) { okHttp.newCall(capture(slot)) }

        /** capture the Request and test the parameters */
//        slot.captured.run {
//            readText().assertEquals(req.toJsonObject().toString())
            // TODO to fix
//            url.toString().assertEquals("https://cdn.sp-stage.net/wrapper/v2/messages?env=localProd")
//            method.assertEquals("POST")
//            url.queryParameter("env").assertEquals("localProd")
//        }
    }

    @Test
    fun `GIVEN a Right Object from parseResponse VERIFY the success callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onResponse(mockk(), mockk()) })
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Right(mockk()))

        /** execution */
//        sut.getMessage(messageReq = req, pSuccess = { successMock(it) }, pError = { errorMock(it) }, stage = Env.STAGE)

        /** verify that the right callback is invoked */
//        verify(exactly = 1) { successMock(any()) }
//        verify(exactly = 0) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a Left Object from parseResponse VERIFY the error callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onResponse(mockk(), mockk()) })
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Left(mockk()))

        /** execution */
//        sut.getMessage(messageReq = req, pSuccess = { successMock(it) }, pError = { errorMock(it) }, stage = Env.STAGE)

        /** verify that the right callback is invoked */
//        verify(exactly = 0) { successMock(any()) }
//        verify(exactly = 1) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a failure from httpClient VERIFY the error callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onFailure(mockk(), mockk()) })
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Left(mockk()))

        /** execution */
//        sut.getMessage(messageReq = req, pSuccess = { successMock(it) }, pError = { errorMock(it) }, stage = Env.STAGE)

        /** verify that the right callback is invoked */
//        verify(exactly = 0) { successMock(any()) }
//        verify(exactly = 1) { errorMock(any()) }
    }

    //    @Test
    fun `GIVEN a UWReq Object and a real endpoint VERIFY that the output is a Right`() = runBlocking<Unit> {

        val responseManager = ResponseManager.create(JsonConverter.create())

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = logger
        )

        val res = sut.getMessage(messageReq = req)

        val output = (res as Either.Right<UnifiedMessageResp>).r
    }

    //    @Test
    fun `GIVEN a UWReq Object VERIFY that the output is a Right`() = runBlocking<Unit> {

        every { responseManager.parseResponse(any()) }.returns(Either.Right(mockk()))

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = logger
        )

        val res = sut.getMessage(messageReq = req)

        val output = (res as Either.Right<UnifiedMessageResp>).r
    }

    //    @Test
    fun `GIVEN an exception VERIFY that the output is a Left`() = runBlocking<Unit> {

        every { responseManager.parseResponse(any()) }.returns(Either.Left(RuntimeException("test")))

        val sut = createNetworkClient(
            httpClient = OkHttpClient(),
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = logger
        )

        val res = sut.getMessage(messageReq = req)

        val output = (res as Either.Left).t
        output.message.assertEquals("test")
    }

    private suspend fun NetworkClient.getMessage(messageReq: MessageReq) = suspendCoroutine<Either<UnifiedMessageResp>> {
//        getMessage(
//            messageReq,
//            { messageResp -> it.resume(Either.Right(messageResp)) },
//            { throwable -> it.resume(Either.Left(throwable)) },
//            Env.STAGE
//        )
    }
}
