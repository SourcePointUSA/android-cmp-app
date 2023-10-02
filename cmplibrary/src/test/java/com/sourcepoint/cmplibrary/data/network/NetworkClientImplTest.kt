package com.sourcepoint.cmplibrary.data.network

import android.content.Context
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.GetChoiceParamReq
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.data.network.util.ResponseManager
import com.sourcepoint.cmplibrary.data.network.util.isInternetConnected
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
    private lateinit var context: Context

    @MockK
    lateinit var okHttp: OkHttpClient

    @MockK
    private lateinit var errorMock: (Throwable) -> Unit

    @MockK
    private lateinit var responseManager: ResponseManager

    @MockK
    private lateinit var urlManager: HttpUrlManager

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        every { context.isInternetConnected() } returns true
    }

    private val sut by lazy {
        createNetworkClient(
            context = context,
            httpClient = okHttp,
            responseManager = responseManager,
            urlManager = urlManager,
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
    fun `sendCustomConsent - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockEnv = mockk<Env>()
        val mockCustomConsentReq = mockk<CustomConsentReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.deleteCustomConsentTo(
            env = mockEnv,
            customConsentReq = mockCustomConsentReq,
        )

        // THEN
        verify(exactly = 0) { urlManager.sendCustomConsentUrl(any()) }
        verify(exactly = 0) { responseManager.parseCustomConsentRes(any()) }
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
    fun `deleteCustomConsentTo - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockEnv = mockk<Env>()
        val mockCustomConsentReq = mockk<CustomConsentReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.deleteCustomConsentTo(
            env = mockEnv,
            customConsentReq = mockCustomConsentReq,
        )

        // THEN
        verify(exactly = 0) { urlManager.deleteCustomConsentToUrl(any(), any()) }
        verify(exactly = 0) { responseManager.parseCustomConsentRes(any()) }
    }

    @Test
    fun `EXECUTE getMetaData and VERIFY that the result is a RIGHT obj`() {
        val respConsent = JSONObject("v7/meta_data.json".file2String())
        val mockResp = mockResponse("https://mock.com", respConsent.toString())
        val mockCall = mockk<Call>()
        every { okHttp.newCall(any()) }.returns(mockCall)
        every { mockCall.execute() }.returns(mockResp)
        every { responseManager.parseMetaDataRes(any()) }.returns(MetaDataResp(null, null))

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
        every { responseManager.parseMetaDataRes(any()) }.returns(MetaDataResp(null, null))

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
        every { responseManager.parseMetaDataRes(any()) }.returns(MetaDataResp(null, null))

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
    fun `getMetaData - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockMetaDataParam = mockk<MetaDataParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.getMetaData(param = mockMetaDataParam)

        // THEN
        verify(exactly = 0) { urlManager.getMetaDataUrl(any()) }
        verify(exactly = 0) { responseManager.parseMetaDataRes(any()) }
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
    fun `getConsentStatus - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockConsentStatusParam = mockk<ConsentStatusParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.getConsentStatus(param = mockConsentStatusParam)

        // THEN
        verify(exactly = 0) { urlManager.getConsentStatusUrl(any()) }
        verify(exactly = 0) { responseManager.parseConsentStatusResp(any()) }
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
    fun `getMessages - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockMessagesParam = mockk<MessagesParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.getMessages(param = mockMessagesParam)

        // THEN
        verify(exactly = 0) { urlManager.getMessagesUrl(any()) }
        verify(exactly = 0) { responseManager.parseMessagesResp(any()) }
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
    fun `postPvData - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockPvDataParam = mockk<PvDataParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.postPvData(param = mockPvDataParam)

        // THEN
        verify(exactly = 0) { urlManager.getPvDataUrl(any()) }
        verify(exactly = 0) { responseManager.parsePvDataResp(any()) }
    }

    @Test
    fun `getChoice - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockChoiceParam = mockk<GetChoiceParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.getChoice(param = mockChoiceParam)

        // THEN
        verify(exactly = 0) { urlManager.getChoiceUrl(any()) }
        verify(exactly = 0) { responseManager.parseGetChoiceResp(any(), any()) }
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
    fun `storeGdprChoice - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockPostChoiceParam = mockk<PostChoiceParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.storeGdprChoice(param = mockPostChoiceParam)

        // THEN
        verify(exactly = 0) { urlManager.getGdprChoiceUrl(any()) }
        verify(exactly = 0) { responseManager.parsePostGdprChoiceResp(any()) }
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

    @Test
    fun `storeCcpaChoice - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        val mockPostChoiceParam = mockk<PostChoiceParamReq>()
        every { context.isInternetConnected() } returns mockIsConnected

        // WHEN
        sut.storeCcpaChoice(param = mockPostChoiceParam)

        // THEN
        verify(exactly = 0) { urlManager.getCcpaChoiceUrl(any()) }
        verify(exactly = 0) { responseManager.parsePostCcpaChoiceResp(any()) }
    }
}
