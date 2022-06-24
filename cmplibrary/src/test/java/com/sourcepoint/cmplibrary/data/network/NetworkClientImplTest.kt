package com.sourcepoint.cmplibrary.data.network

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.data.network.model.toJsonObject
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CustomConsentReq
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.readText
import com.sourcepoint.cmplibrary.stub.MockCall
import com.sourcepoint.cmplibrary.stub.MockLogger
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

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    private val sut by lazy {
        createNetworkClient(
            httpClient = okHttp,
            responseManager = responseManager,
            urlManager = HttpUrlManagerSingleton,
            logger = MockLogger
        )
    }

    @Test
    fun `GIVEN a UWReq Object in PROD EXECUTE getUnifiedMessage and VERIFY the okHttp generated the PROD request`() {
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
    fun `GIVEN a UWReq Object in STAGE EXECUTE getUnifiedMessage and VERIFY the okHttp generated the STAGE request`() {
        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.STAGE)

        val slot = slot<Request>()
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }

        /** capture the Request and test the parameters */
        slot.captured.run {
            readText().assertEquals(uwMessDataTest.toJsonObject().toString())
            url.toString().assertEquals("https://cdn.sp-stage.net/wrapper/v2/get_messages?env=${BuildConfig.ENV_QUERY_PARAM}")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals(BuildConfig.ENV_QUERY_PARAM)
        }
    }

    @Test
    fun `GIVEN a Right Object from parseResponse EXECUTE getUnifiedMessage and VERIFY the success callback is called`() {
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
    fun `GIVEN a Left Object from parseResponse EXECUTE getUnifiedMessage and VERIFY the error callback is called`() {
        /** preconditions */
        val mockCall = MockCall(logicResponseCB = { cb -> cb.onResponse(mockk(), mockk()) })
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { responseManager.parseResponse(any()) }.returns(Either.Left(RuntimeException()))

        /** execution */
        sut.getUnifiedMessage(messageReq = uwMessDataTest, pSuccess = { successMock(it) }, pError = { errorMock(it) }, env = Env.STAGE)

        /** verify that the right callback is invoked */
        verify(exactly = 0) { successMock(any()) }
        verify(exactly = 1) { errorMock(any()) }
    }

    @Test
    fun `GIVEN a failure from httpClient EXECUTE getUnifiedMessage and VERIFY the error callback is called`() {
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
    fun `GIVEN a GDPR consentAction Object in STAGE EXECUTE sendConsent and VERIFY the okHttp generated the STAGE request`() {
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
            url.toString().assertEquals("https://cdn.sp-stage.net/wrapper/v2/messages/choice/gdpr/11?env=${BuildConfig.ENV_QUERY_PARAM}")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals(BuildConfig.ENV_QUERY_PARAM)
        }
    }

    @Test
    fun `GIVEN a crash in the responseManager EXECUTE sendConsent and RETURN a Left`() {
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

    @Test
    fun `EXECUTE sendCustomConsent and VERIFY that the okHttp generated the STAGE request`() {
        val request = JSONObject("{\"consentUUID\":\"uuid\",\"categories\":[\"b\"],\"propertyId\":1,\"vendors\":[],\"legIntCategories\":[\"a\"]}").toTreeMap()
        val mockResp = mockResponse("https://mock.com", uwMessDataTest.toJsonObject().toString())
        val mockCall = mockk<Call>()
        val slot = slot<Request>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        sut.sendCustomConsent(req, Env.STAGE)

        verify(exactly = 1) { responseManager.parseCustomConsentRes(mockResp) }
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }
        /** capture the Request and test the parameters */
        slot.captured.run {
            readText().let { JSONObject(it).toTreeMap() }.assertEquals(request)
            url.toString().assertEquals("https://cdn.sp-stage.net/wrapper/tcfv2/v1/gdpr/custom-consent?env=${BuildConfig.ENV_QUERY_PARAM}&inApp=true")
            method.assertEquals("POST")
            url.queryParameter("env").assertEquals(BuildConfig.ENV_QUERY_PARAM)
        }
    }

    @Test
    fun `EXECUTE deleteCustomConsentTo and VERIFY that the okHttp generated the PROD request`() {
        val request = JSONObject("{\"categories\":[\"b\"],\"vendors\":[],\"legIntCategories\":[\"a\"]}").toTreeMap()
        val mockResp = mockResponse("https://mock.com", uwMessDataTest.toJsonObject().toString())
        val mockCall = mockk<Call>()
        val slot = slot<Request>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        sut.deleteCustomConsentTo(req, Env.PROD)

        verify(exactly = 1) { responseManager.parseCustomConsentRes(mockResp) }
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }
        /** capture the Request and test the parameters */
        slot.captured.run {
            readText().let { JSONObject(it).toTreeMap() }.assertEquals(request)
            url.toString().assertEquals("https://cdn.privacy-mgmt.com/consent/tcfv2/consent/v3/custom/${req.propertyId}?consentUUID=${req.consentUUID}")
            method.assertEquals("DELETE")
        }
    }

    @Test
    fun `EXECUTE sendCustomConsent and VERIFY that the result is a RIGHT obj`() {
        val respConsent = JSONObject("custom_consent/custom_consent_res.json".file2String())
        val mockResp = mockResponse("https://mock.com", respConsent.toString())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseCustomConsentRes(any()) }.returns(CustomConsentResp(respConsent))

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        val res = sut.sendCustomConsent(req, Env.STAGE) as? Either.Right
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE deleteCustomConsentTo and VERIFY that the result is a RIGHT obj`() {
        val respConsent = JSONObject("custom_consent/custom_consent_res.json".file2String())
        val mockResp = mockResponse("https://mock.com", respConsent.toString())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseCustomConsentRes(any()) }.returns(CustomConsentResp(respConsent))

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        val res = sut.deleteCustomConsentTo(req, Env.STAGE) as? Either.Right
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE sendCustomConsent parseCustomConsentRes THROWS an exception and the result is a LEFT obj`() {
        val respConsent = JSONObject("custom_consent/custom_consent_res.json".file2String())
        val mockResp = mockResponse("https://mock.com", respConsent.toString())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseCustomConsentRes(any()) }.throws(RuntimeException("exception"))

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        val res = sut.sendCustomConsent(req, Env.STAGE) as? Either.Left
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE deleteCustomConsentTo parseCustomConsentRes THROWS an exception and the result is a LEFT obj`() {
        val respConsent = JSONObject("custom_consent/custom_consent_res.json".file2String())
        val mockResp = mockResponse("https://mock.com", respConsent.toString())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseCustomConsentRes(any()) }.throws(RuntimeException("exception"))

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        val res = sut.deleteCustomConsentTo(req, Env.STAGE) as? Either.Left
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE sendCustomConsent okHttp THROWS an exception and the result is a LEFT obj`() {
        val respConsent = JSONObject("custom_consent/custom_consent_res.json".file2String())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(RuntimeException("exception"))
        every { responseManager.parseCustomConsentRes(any()) }.returns(CustomConsentResp(respConsent))

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        val res = sut.sendCustomConsent(req, Env.STAGE) as? Either.Left
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE deleteCustomConsentTo okHttp THROWS an exception and the result is a LEFT obj`() {
        val respConsent = JSONObject("custom_consent/custom_consent_res.json".file2String())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(RuntimeException("exception"))
        every { responseManager.parseCustomConsentRes(any()) }.returns(CustomConsentResp(respConsent))

        val req = CustomConsentReq(
            consentUUID = "uuid",
            legIntCategories = listOf("a"),
            categories = listOf("b"),
            vendors = listOf(),
            propertyId = 1
        )

        val res = sut.deleteCustomConsentTo(req, Env.STAGE) as? Either.Left
        res.assertNotNull()
    }
}
