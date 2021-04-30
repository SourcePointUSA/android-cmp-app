package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.ext.toJsonObject
import com.sourcepoint.cmplibrary.model.toConsentAction
import com.sourcepoint.cmplibrary.readText
import com.sourcepoint.cmplibrary.stub.MockCall
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.cmplibrary.uwMessDataTest
import io.mockk.* //ktlint-disable
import io.mockk.impl.annotations.MockK
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

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

    private val sut by lazy {
        createNetworkClient(
            httpClient = okHttp,
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = logger
        )
    }

//    val umr = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()

    @Test
    fun `GIVEN a UWReq Object in PROD VERIFY the okHttp generated the PROD request`() {
        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.PROD)

        val slot = slot<Request>()
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }

        /** capture the Request and test the parameters */
        slot.captured.run {
            readText().assertEquals(uwMessDataTest.toJsonObject().toString())
            url.toString().assertEquals("https://cdn.privacy-mgmt.com/wrapper/v2/get_messages?env=prod")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals("prod")
        }
    }

    @Test
    fun `GIVEN a UWReq Object in STAGE VERIFY the okHttp generated the STAGE request`() {
        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.STAGE)

        val slot = slot<Request>()
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }

        /** capture the Request and test the parameters */
        slot.captured.run {
            readText().assertEquals(uwMessDataTest.toJsonObject().toString())
            url.toString().assertEquals("https://cdn.sp-stage.net/wrapper/v2/get_messages?env=stage")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals("stage")
        }
    }

    @Test
    fun `GIVEN a Right Object from parseResponse VERIFY the success callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onResponse(mockk(), mockk()) })
        val res = mockk<UnifiedMessageResp>()
        every { res.thisContent }.returns(JSONObject())
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Right(res))

        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.STAGE)

        /** verify that the right callback is invoked */
        verify(exactly = 1) { successMock(any()) }
        verify(exactly = 0) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a Left Object from parseResponse VERIFY the error callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onResponse(mockk(), mockk()) })
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Left(mockk()))

        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.STAGE)

        /** verify that the right callback is invoked */
        verify(exactly = 0) { successMock(any()) }
        verify(exactly = 1) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a failure from httpClient VERIFY the error callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onFailure(mockk(), mockk()) })
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Left(mockk()))

        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.STAGE)

        /** verify that the right callback is invoked */
        verify(exactly = 0) { successMock(any()) }
        verify(exactly = 1) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a GDPR consentAction Object in STAGE VERIFY the okHttp generated the STAGE request`() {
        val consentAction = "action/gdpr_pm_accept_all.json".file2String().toConsentAction()
        val mockResp = mockResponse("https://mock.com", uwMessDataTest.toJsonObject().toString())
        val mockCall = mockk<Call>()
        val slot = slot<Request>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)

        sut.sendConsent(JSONObject(), Env.STAGE, consentAction)

        verify(exactly = 1) { responseManager.parseConsentRes(mockResp, CampaignType.GDPR) }
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }
        /** capture the Request and test the parameters */
        slot.captured.run {
            readText().assertEquals("{}")
            url.toString().assertEquals("https://cdn.sp-stage.net/wrapper/v2/messages/choice/gdpr/11?env=stage")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals("stage")
        }
    }

    @Test
    fun `GIVEN a creash in the responseManager RETURN a Left`() {
        val consentAction = "action/gdpr_pm_accept_all.json".file2String().toConsentAction()
        val mockResp = mockResponse("https://mock.com", uwMessDataTest.toJsonObject().toString())
        val mockCall = mockk<Call>()
        val slot = slot<Request>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseConsentRes(any(), any()) }.throws(RuntimeException("parse"))

        val left = sut.sendConsent(JSONObject(), Env.STAGE, consentAction) as Either.Left
        left.t.message.assertEquals("parse")
    }
}
