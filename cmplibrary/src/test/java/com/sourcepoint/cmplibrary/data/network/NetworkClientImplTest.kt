package com.sourcepoint.cmplibrary.data.network

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManagerSingleton
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.exception.ApiRequestPostfix
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.CodeList
import com.sourcepoint.cmplibrary.exception.ConnectionTimeoutException
import com.sourcepoint.cmplibrary.model.CustomConsentReq
import com.sourcepoint.cmplibrary.model.CustomConsentResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.readText
import com.sourcepoint.cmplibrary.stub.MockLogger
import com.sourcepoint.cmplibrary.util.file2String
import io.mockk.* // ktlint-disable
import io.mockk.impl.annotations.MockK
import kotlinx.serialization.json.* // ktlint-disable
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import java.io.InterruptedIOException

class NetworkClientImplTest {

    @MockK
    lateinit var okHttp: OkHttpClient

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

    @Test
    fun `EXECUTE getMetaData and VERIFY that the result is a RIGHT obj`() {
        val respConsent = JSONObject("v7/meta_data.json".file2String())
        val mockResp = mockResponse("https://mock.com", respConsent.toString())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseMetaDataRes(any()) }.returns(MetaDataResp(null, null, null))

        val param = MetaDataParamReq(
            accountId = 22,
            propertyId = 17801,
            metadata = JSONObject("""{"gdpr": {}, "ccpa": {}}""").toString(),
            env = Env.LOCAL_PROD
        )

        val res = sut.getMetaData(param) as? Either.Right<MetaDataResp>
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE getMetaData THROWS an exception and the result is a LEFT obj`() {
        val respConsent = JSONObject("v7/meta_data.json".file2String())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(RuntimeException("exception"))
        every { responseManager.parseMetaDataRes(any()) }.returns(MetaDataResp(null, null, null))

        val param = MetaDataParamReq(
            accountId = 22,
            propertyId = 17801,
            metadata = JSONObject("""{"gdpr": {}, "ccpa": {}}""").toString(),
            env = Env.LOCAL_PROD
        )

        val res = sut.getMetaData(param) as? Either.Left
        res.assertNotNull()
    }

    @Test
    fun `EXECUTE getMetaData THROWS an InterruptedIOException and the result is a LEFT obj`() {
        val respConsent = JSONObject()
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(InterruptedIOException("exception"))
        every { responseManager.parseMetaDataRes(any()) }.returns(MetaDataResp(null, null, null))

        val param = MetaDataParamReq(
            accountId = 22,
            propertyId = 17801,
            metadata = JSONObject("""{"gdpr": {}, "ccpa": {}}""").toString(),
            env = Env.LOCAL_PROD
        )

        val res = sut.getMetaData(param) as? Either.Left
        (res!!.t as ConnectionTimeoutException).code.errorCode.assertEquals(CodeList.CONNECTION_TIMEOUT.errorCode + ApiRequestPostfix.META_DATA.apiPostfix)
    }

    @Test
    fun `EXECUTE getConsentStatus THROWS an InterruptedIOException and the result is a LEFT obj`() {
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(InterruptedIOException("exception"))

        val param = ConsentStatusParamReq(
            accountId = 22,
            propertyId = 17801,
            metadata = JSONObject().toString(),
            env = Env.LOCAL_PROD,
            authId = null,
            localState = null
        )

        val res = sut.getConsentStatus(param) as? Either.Left
        (res!!.t as ConnectionTimeoutException).code.errorCode.assertEquals(CodeList.CONNECTION_TIMEOUT.errorCode + ApiRequestPostfix.CONSENT_STATUS.apiPostfix)
    }

    @Test
    fun `EXECUTE getMessages THROWS an InterruptedIOException and the result is a LEFT obj`() {
        val mockCall = mockk<Call>()

        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(InterruptedIOException("exception"))

        val param = MessagesParamReq(
            accountId = 1,
            propertyId = 1,
            authId = null,
            propertyHref = "prop",
            env = Env.LOCAL_PROD,
            metadataArg = null,
            body = "{}",
        )

        val res = sut.getMessages(param) as? Either.Left
        (res!!.t as ConnectionTimeoutException).code.errorCode.assertEquals(CodeList.CONNECTION_TIMEOUT.errorCode + ApiRequestPostfix.MESSAGES.apiPostfix)
    }

    @Test
    fun `EXECUTE savePvData THROWS an InterruptedIOException and the result is a LEFT obj`() {
        val mockCall = mockk<Call>()

        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.throws(InterruptedIOException("exception"))

        val param = PvDataParamReq(
            env = Env.LOCAL_PROD,
            body = buildJsonObject { },
            campaignType = CampaignType.GDPR
        )

        val res = sut.postPvData(param) as? Either.Left
        (res!!.t as ConnectionTimeoutException).code.errorCode.assertEquals(CodeList.CONNECTION_TIMEOUT.errorCode + ApiRequestPostfix.PV_DATA.apiPostfix)
    }

    @Test
    fun `storeGdprChoice - WHEN executed with pubData in request params THEN should have pubData in request for GDPR`() {
        // GIVEN
        val slot = slot<Request>()
        val mockResponse = mockk<Response>()
        val mockCall = mockk<Call>()
        val mockBody = JsonObject(
            mapOf(
                "pb_key" to JsonPrimitive("pb_value")
            )
        )
        val mockRequest = PostChoiceParamReq(
            env = Env.PROD,
            actionType = ActionType.ACCEPT_ALL,
            body = mockBody
        )

        // WHEN
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResponse)
        sut.storeGdprChoice(mockRequest)

        // THEN
        verify(exactly = 1) { responseManager.parsePostGdprChoiceResp(mockResponse) }
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }
        slot.captured.run {
            readText().let { Json.parseToJsonElement(it).jsonObject }.assertEquals(mockBody)
            method.assertEquals("POST")
        }
    }

    @Test
    fun `storeCcpaChoice - WHEN executed with pubData in request params THEN should have pubData in request for GDPR`() {
        // GIVEN
        val slot = slot<Request>()
        val mockResponse = mockk<Response>()
        val mockCall = mockk<Call>()
        val mockBody = JsonObject(
            mapOf(
                "pb_key" to JsonPrimitive("pb_value")
            )
        )
        val mockRequest = PostChoiceParamReq(
            env = Env.PROD,
            actionType = ActionType.ACCEPT_ALL,
            body = mockBody
        )

        // WHEN
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResponse)
        sut.storeCcpaChoice(mockRequest)

        // THEN
        verify(exactly = 1) { responseManager.parsePostCcpaChoiceResp(mockResponse) }
        verify(exactly = 1) { okHttp.newCall(capture(slot)) }
        slot.captured.run {
            readText().let { Json.parseToJsonElement(it).jsonObject }.assertEquals(mockBody)
            method.assertEquals("POST")
        }
    }
}
