package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.* // ktlint-disable
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.messagesParamReq
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import com.sourcepoint.cmplibrary.util.file2String
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class ServiceImplTest {

    @MockK
    private lateinit var ncMock: NetworkClient

    @MockK
    private lateinit var ds: DataStorage

    @MockK
    private lateinit var cm: CampaignManager

    @MockK
    private lateinit var cmu: ConsentManagerUtils

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var mockMetaDataResp: MetaDataResp

    @MockK
    private lateinit var mockConsentStatusResp: ConsentStatusResp

    @MockK
    private lateinit var mockMessagesResp: MessagesResp

    @MockK
    private lateinit var mockPvDataResp: PvDataResp

    @MockK
    private lateinit var execManager: ExecutorManager

    @MockK
    private lateinit var connectionManager: ConnectionManager

    @MockK
    private lateinit var successMockV7: (MessagesResp) -> Unit

    @MockK
    private lateinit var consentMockV7: () -> Unit

    @MockK
    private lateinit var errorMock: (Throwable, Boolean) -> Unit

    private val nativeCampaign = Campaign(
        accountId = 22,
        propertyName = "tcfv2.mobile.demo",
        pmId = "179657"
    )

    private val spConfig = SpConfig(
        22,
        "asfa",
        emptyList(),
        MessageLanguage.ENGLISH,
        propertyId = 1234,
        messageTimeout = 3000,
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)

        every { connectionManager.isConnected } returns true
    }

    @Test
    fun `sendCustomConsentServ - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockCustomConsentReq: CustomConsentReq = mockk()
        val mockEnv = Env.PROD
        every { connectionManager.isConnected } returns false

        // WHEN
        val service = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        service.sendCustomConsentServ(mockCustomConsentReq, mockEnv)

        // THEN
        verify(exactly = 0) { ncMock.sendCustomConsent(any(), any()) }
    }

    @Test
    fun `deleteCustomConsentToServ - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockCustomConsentReq: CustomConsentReq = mockk()
        val mockEnv = Env.PROD
        every { connectionManager.isConnected } returns false

        // WHEN
        val service = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        service.deleteCustomConsentToServ(mockCustomConsentReq, mockEnv)

        // THEN
        verify(exactly = 0) { ncMock.deleteCustomConsentTo(any(), any()) }
    }

    @Test
    fun `getMessages - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockMessagesParamReq: MessagesParamReq = mockk()
        every { connectionManager.isConnected } returns false

        // WHEN
        val service = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        service.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        // THEN
        verify(exactly = 0) { ncMock.getMetaData(any()) }
        verify(exactly = 0) { ncMock.getConsentStatus(any()) }
        verify(exactly = 0) { ncMock.getMessages(any()) }
        verify(exactly = 0) { ncMock.postPvData(any()) }
    }

    @Test
    fun `GIVEN a custom consent UPDATE the stored consent`() {
        val storedConsent = "custom_consent/stored_consent.json".file2String()
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.returns(storedConsent)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        val res = sut.sendCustomConsent(mockk(), Env.STAGE).getOrNull()!!
        res.content.getJSONObject("grants").toTreeMap()
            .assertEquals(JSONObject(newConsent).getJSONObject("grants").toTreeMap())
    }

    @Test
    fun `GIVEN a custom consent VERIFY that the data storage is called`() {
        val storedConsentString = "custom_consent/consent_status_optimized.json".file2String()
        val storedConsent = JsonConverter.converter.decodeFromString<GdprCS>(storedConsentString)
        val newConsent = "custom_consent/new_consent.json".file2String()

        // The Grants in stored consent are ALL true because an action of Accept All
        storedConsent.grants!!.toList().flatMap { i -> i.second.purposeGrants.values }.all { e -> e }.assertTrue()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { cm.gdprConsentStatus } answers { storedConsent }

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        sut.sendCustomConsentServ(mockk(), Env.STAGE).getOrNull()!!

        verify(exactly = 1) {
            cm.gdprConsentStatus = withArg {
                // The Grants in stored consent are NOT ALL true because the custom consent edited the values
                it.grants!!.toList().flatMap { i -> i.second.purposeGrants.values }.all { e -> e }.assertFalse()
            }
        }
    }

    @Test
    fun `GIVEN a deleted custom consent VERIFY that the data storage is called`() {
        val storedConsentString = "custom_consent/consent_status_optimized.json".file2String()
        val storedConsent = JsonConverter.converter.decodeFromString<GdprCS>(storedConsentString)
        val newConsent = "custom_consent/new_consent.json".file2String()

        // The Grants in stored consent are ALL true because an action of Accept All
        storedConsent.grants!!.toList().flatMap { i -> i.second.purposeGrants.values }.all { e -> e }.assertTrue()

        every { ncMock.deleteCustomConsentTo(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { cm.gdprConsentStatus } answers { storedConsent }

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        sut.deleteCustomConsentToServ(mockk(), Env.STAGE).getOrNull()!!

        verify(exactly = 1) {
            cm.gdprConsentStatus = withArg {
                // The Grants in stored consent are NOT ALL true because the custom consent edited the values
                it.grants!!.toList().flatMap { i -> i.second.purposeGrants.values }.all { e -> e }.assertFalse()
            }
        }
    }

    @Test
    fun `GIVEN a custom consent THROWS an exception`() {
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.sendCustomConsent(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { cm.gdprConsentStatus } answers { null }

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        val res = sut.sendCustomConsentServ(mockk(), Env.STAGE)
        (res as? Left).assertNotNull()
    }

    @Test
    fun `GIVEN a deleted custom consent THROWS an exception`() {
        val newConsent = "custom_consent/new_consent.json".file2String()

        every { ncMock.deleteCustomConsentTo(any(), any()) }.returns(Right(CustomConsentResp(JSONObject(newConsent))))
        every { ds.getGdprConsentResp() }.throws(RuntimeException("test"))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager)
        val res = sut.sendCustomConsentServ(mockk(), Env.STAGE)
        (res as? Left).assertNotNull()
    }

    @Test
    fun `GIVEN a MetaData resp VERIFY that the consentStatus call is executed`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        every { ncMock.getMetaData(any()) }.returns(Either.Right(metadata))
        every { cm.shouldCallConsentStatus(any()) }.returns(true)
        every { cm.spConfig }.returns(spConfig)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

//        verify(exactly = 0) { errorMock(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any()) }
    }

    @Test
    fun `GIVEN a consentStatus resp VERIFY that the consentStatus is saved`() {

        val metadataJson = "v7/meta_data.json".file2String()
        val metadata = JsonConverter.converter.decodeFromString<MetaDataResp>(metadataJson)

        val consentStatusJson = "v7/consent_status_with_auth_id.json".file2String()
        val consentStatus = JsonConverter.converter.decodeFromString<ConsentStatusResp>(consentStatusJson)

        every { ncMock.getMetaData(any()) }.returns(Either.Right(metadata))
        every { ncMock.getConsentStatus(any()) }.returns(Either.Right(consentStatus))
        every { cm.shouldCallConsentStatus(any()) }.returns(true)
        every { cm.spConfig }.returns(spConfig)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        // TODO
    }

    @Test
    fun `GIVEN a Left during getMetaData req RETURN call the error callback`() {

        val messageJson = "v7/messagesObj.json".file2String()
        val messageResp = JsonConverter.converter.decodeFromString<MessagesResp>(messageJson)

        every { ncMock.getMetaData(any()) }.returns(Left(RuntimeException()))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        verify(exactly = 1) { errorMock(any(), any()) }
        verify(exactly = 0) { successMockV7(any()) }
        verify(exactly = 0) { consentMockV7() }
    }

    @Test
    fun `GIVEN a Left during getMetaData req CALL onError`() {

        val mockCampaignsList = listOf(
            SpCampaign(
                campaignType = CampaignType.GDPR
            ),
            SpCampaign(
                campaignType = CampaignType.CCPA
            ),
        )

        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { cm.shouldCallConsentStatus(any()) } returns true
        every { ncMock.getMetaData(any()) }.returns(Right(mockMetaDataResp))
        every { ncMock.getConsentStatus(any()) }.returns(Left(RuntimeException()))

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        sut.getMessages(
            messageReq = messagesParamReq.copy(authId = "test"),
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        verify(exactly = 1) { errorMock(any(), any()) }
        verify(exactly = 0) { successMockV7(any()) }
        verify(exactly = 0) { consentMockV7() }
    }

    @Test
    fun `GIVEN a Left object during the getConsentStatus req CALL the error cb`() {

        val mockMessagesParamReq = messagesParamReq.copy(
            authId = "mock_auth_id"
        )
        val mockCampaignsList = listOf(
            SpCampaign(
                campaignType = CampaignType.GDPR
            ),
            SpCampaign(
                campaignType = CampaignType.CCPA
            ),
        )
        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { ncMock.getMetaData(any()) }.returns(Right(mockMetaDataResp))
        every { ncMock.getConsentStatus(any()) }.returns(Right(mockConsentStatusResp))
        every { ncMock.getMessages(any()) }.returns(Left(RuntimeException()))
        every { cm.messageLanguage } returns MessageLanguage.ENGLISH
        every { cm.shouldCallMessages }.returns(true)
        every { cm.messagesOptimizedLocalState }.returns(JsonObject(emptyMap()))
        every { cm.nonKeyedLocalState }.returns(JsonObject(emptyMap()))
        every { cm.campaigns4Config }.returns(emptyList())

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        sut.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        verify(exactly = 1) { errorMock(any(), any()) }
        verify(exactly = 0) { successMockV7(any()) }
        verify(exactly = 0) { consentMockV7() }
    }

    /**
     * Regression tests case that verifies if the UUID is always being present in the consent status
     * object
     */
    @Test
    fun `getMessages - WHEN called THEN should always have UUIDs for GDPR and CCPA`() {
        // GIVEN
        val mockMessagesParamReq = messagesParamReq.copy(
            authId = "mock_auth_id"
        )
        val mockCampaignsList = listOf(
            SpCampaign(
                campaignType = CampaignType.GDPR
            ),
            SpCampaign(
                campaignType = CampaignType.CCPA
            ),
        )

        // WHEN
        every { cm.shouldCallMessages } returns true
        every { cm.shouldCallConsentStatus(any()) } returns true
        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { cm.messagesOptimizedLocalState } returns JsonObject(emptyMap())
        every { cm.nonKeyedLocalState } returns JsonObject(emptyMap())
        every { cmu.shouldTriggerByGdprSample } returns true
        every { cmu.shouldTriggerByCcpaSample } returns true
        every { ncMock.getMetaData(any()) } returns Right(mockMetaDataResp)
        every { ncMock.getMessages(any()) } returns Right(mockMessagesResp)
        every { ncMock.postPvData(any()) } returns Right(mockPvDataResp)

        val service = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        service.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        // THEN
        verify(exactly = 1) { ncMock.getMetaData(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any()) }
        verify(exactly = 1) { ncMock.getMessages(any()) }
        verify(exactly = 2) { ncMock.postPvData(any()) }
    }

    /**
     * Test case which verifies that uspstring is being updated each time getMessages() being called
     */
    @Test
    fun `getMessages - WHEN called THEN should update uspstring in data storage`() {
        // GIVEN
        val mockMessagesParamReq = messagesParamReq.copy(
            authId = "mock_auth_id"
        )
        val mockCampaignsList = listOf(
            SpCampaign(
                campaignType = CampaignType.GDPR
            ),
            SpCampaign(
                campaignType = CampaignType.CCPA
            ),
        )

        // WHEN
        every { cm.shouldCallMessages } returns true
        every { cm.shouldCallConsentStatus(any()) } returns true
        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { cm.messagesOptimizedLocalState } returns JsonObject(emptyMap())
        every { cm.nonKeyedLocalState } returns JsonObject(emptyMap())
        every { cmu.shouldTriggerByGdprSample } returns true
        every { cmu.shouldTriggerByCcpaSample } returns true
        every { ncMock.getMetaData(any()) } returns Right(mockMetaDataResp)
        every { ncMock.getConsentStatus(any()) } returns Right(mockConsentStatusResp)
        every { ncMock.getMessages(any()) } returns Right(mockMessagesResp)
        every { ncMock.postPvData(any()) } returns Right(mockPvDataResp)
        val service = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager)
        service.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        // THEN
        verify(exactly = 1) { ncMock.getMetaData(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any()) }
        verify(exactly = 1) { ncMock.getMessages(any()) }
        verify(exactly = 2) { ncMock.postPvData(any()) }
        verify(atLeast = 2) { cm.ccpaConsentStatus = any() }
    }
}
