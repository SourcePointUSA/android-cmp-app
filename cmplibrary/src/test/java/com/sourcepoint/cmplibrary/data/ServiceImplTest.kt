package com.sourcepoint.cmplibrary.data

import com.sourcepoint.cmplibrary.* // ktlint-disable
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.core.Either.Right
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.NetworkClient
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.model.optimized.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.messagesParamReq
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.stub.MockExecutorManager
import com.sourcepoint.mobile_core.Coordinator
import com.sourcepoint.mobile_core.network.responses.ConsentStatusResponse
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import com.sourcepoint.mobile_core.network.responses.PvDataResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.serialization.json.JsonObject
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
    private lateinit var mockMetaDataResp: MetaDataResponse

    private val mockConsentStatusResp: ConsentStatusResponse get() {
        val mock = mockk<ConsentStatusResponse>(relaxed = true)
        every { mock.localState } returns "\"\""
        return mock
    }

    @MockK
    private lateinit var mockMessagesResp: MessagesResp

    @MockK
    private lateinit var execManager: ExecutorManager

    @MockK
    private lateinit var connectionManager: ConnectionManager

    @MockK
    private lateinit var coreCoordinator: Coordinator

    @MockK
    private lateinit var successMockV7: (MessagesResp) -> Unit

    @MockK
    private lateinit var consentMockV7: () -> Unit

    @MockK
    private lateinit var errorMock: (Throwable, Boolean) -> Unit

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
        every { connectionManager.isConnected } returns false

        // WHEN
        val service = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager, coreCoordinator)
        service.sendCustomConsentServ(
            "uuid",
            123,
            mockk(),
            mockk(),
            mockk()
        )

        // THEN
        verify(exactly = 0) {
            ncMock.sendCustomConsent(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `deleteCustomConsentToServ - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        every { connectionManager.isConnected } returns false

        // WHEN
        val service = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager, coreCoordinator)
        service.deleteCustomConsentToServ(
            "uuid",
            123,
            mockk(),
            mockk(),
            mockk()
        )

        // THEN
        verify(exactly = 0) {
            ncMock.deleteCustomConsentTo(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun `getMessages - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockMessagesParamReq: MessagesParamReq = mockk()
        every { connectionManager.isConnected } returns false

        // WHEN
        val service = Service.create(ncMock, cm, cmu, ds, logger, execManager, connectionManager, coreCoordinator)
        service.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        // THEN
        verify(exactly = 0) { ncMock.getMetaData(any()) }
        verify(exactly = 0) { ncMock.getConsentStatus(any(), any()) }
        verify(exactly = 0) { ncMock.getMessages(any()) }
        verify(exactly = 0) { ncMock.postPvData(any()) }
    }

    @Test
    fun `GIVEN a custom consent THROWS an exception`() {
        every { cm.gdprConsentStatus } answers { null }

        (
            Service
                .create(ncMock, cm, cmu, ds, logger, execManager, connectionManager, coreCoordinator)
                .sendCustomConsentServ("uuid", 123, mockk(), mockk(), mockk()) as? Left
            ).assertNotNull()
    }

    @Test
    fun `GIVEN a deleted custom consent THROWS an exception`() {
        every { cm.gdprConsentStatus } answers { null }

        (
            Service
                .create(ncMock, cm, cmu, ds, logger, execManager, connectionManager, coreCoordinator)
                .deleteCustomConsentToServ("uuid", 123, mockk(), mockk(), mockk()) as? Left
            ).assertNotNull()
    }

    @Test
    fun `GIVEN a shouldCallConsentStatus returns true, verify that the consentStatus call is executed`() {
        every { cm.shouldCallConsentStatus(any()) }.returns(true)
        every { cm.spConfig }.returns(spConfig)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        verify(exactly = 1) { ncMock.getConsentStatus(any(), any()) }
    }

    @Test
    fun `GIVEN a consentStatus resp VERIFY that the consentStatus is saved`() {
        every { cm.shouldCallConsentStatus(any()) }.returns(true)
        every { cm.spConfig }.returns(spConfig)

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
        sut.getMessages(
            messageReq = messagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )
    }

    @Test
    fun `GIVEN a Left during getMetaData req RETURN call the error callback`() {
        every { ncMock.getMetaData(any()) }.throws(RuntimeException())

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
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
            SpCampaign(CampaignType.GDPR),
            SpCampaign(CampaignType.CCPA),
        )

        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { cm.shouldCallConsentStatus(any()) } returns true
        every { ncMock.getMetaData(any()) }.returns(mockMetaDataResp)
        every { ncMock.getConsentStatus(any(), any()) }.throws(RuntimeException())

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
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
        val mockMessagesParamReq = messagesParamReq.copy(authId = "mock_auth_id")
        val mockCampaignsList = listOf(
            SpCampaign(CampaignType.GDPR),
            SpCampaign(CampaignType.CCPA),
        )
        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { ncMock.getMetaData(any()) }.returns(mockMetaDataResp)
        every { ncMock.getConsentStatus(any(), any()) }.returns(mockConsentStatusResp)
        every { ncMock.getMessages(any()) }.returns(Left(RuntimeException()))
        every { cm.messageLanguage } returns MessageLanguage.ENGLISH
        every { cm.shouldCallMessages }.returns(true)
        every { cm.messagesOptimizedLocalState }.returns(null)
        every { cm.nonKeyedLocalState }.returns(JsonObject(emptyMap()))
        every { cm.campaigns4Config }.returns(emptyList())

        val sut = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
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
        val mockMessagesParamReq = messagesParamReq.copy(authId = "mock_auth_id")
        val mockCampaignsList = listOf(
            SpCampaign(CampaignType.GDPR),
            SpCampaign(CampaignType.CCPA),
        )

        every { cm.shouldCallMessages } returns true
        every { cm.shouldCallConsentStatus(any()) } returns true
        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { cm.messagesOptimizedLocalState } returns null
        every { cm.nonKeyedLocalState } returns JsonObject(emptyMap())
        every { ds.ccpaSampled } returns null
        every { ds.gdprSampled } returns null
        every { cmu.sample(any()) } returns true
        every { ncMock.getMetaData(any()) } returns mockMetaDataResp
        every { ncMock.getConsentStatus(any(), any()) } returns mockConsentStatusResp
        every { ncMock.getMessages(any()) } returns Right(mockMessagesResp)
        every { ncMock.postPvData(any()) } returns PvDataResponse(gdpr = null,ccpa = null,usnat = null)

        val service = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
        service.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        verify(exactly = 1) { ncMock.getMetaData(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any(), any()) }
        verify(exactly = 1) { ncMock.getMessages(any()) }
        verify(exactly = 2) { ncMock.postPvData(any()) }
    }

    /**
     * Test case which verifies that uspstring is being updated each time getMessages() being called
     */
    @Test
    fun `getMessages - WHEN called THEN should update uspstring in data storage`() {
        val mockMessagesParamReq = messagesParamReq.copy(authId = "mock_auth_id")
        val mockCampaignsList = listOf(
            SpCampaign(CampaignType.GDPR),
            SpCampaign(CampaignType.CCPA),
        )

        every { cm.shouldCallMessages } returns true
        every { cm.shouldCallConsentStatus(any()) } returns true
        every { cm.spConfig } returns spConfig.copy(campaigns = mockCampaignsList)
        every { cm.messagesOptimizedLocalState } returns null
        every { cm.nonKeyedLocalState } returns JsonObject(emptyMap())
        every { ds.ccpaSampled } returns null
        every { ds.gdprSampled } returns null
        every { cmu.sample(any()) } returns true
        every { ncMock.getMetaData(any()) } returns mockMetaDataResp
        every { ncMock.getConsentStatus(any(), any()) } returns mockConsentStatusResp
        every { ncMock.getMessages(any()) } returns Right(mockMessagesResp)
        every { ncMock.postPvData(any()) } returns PvDataResponse(gdpr = null,ccpa = PvDataResponse.Campaign("1"),usnat = null)
        val service = Service.create(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
        service.getMessages(
            messageReq = mockMessagesParamReq,
            showConsent = consentMockV7,
            onSuccess = successMockV7,
            onFailure = errorMock,
        )

        verify(exactly = 1) { ncMock.getMetaData(any()) }
        verify(exactly = 1) { ncMock.getConsentStatus(any(), any()) }
        verify(exactly = 1) { ncMock.getMessages(any()) }
        verify(exactly = 2) { ncMock.postPvData(any()) }
        verify(atLeast = 2) { cm.ccpaConsentStatus = any() }
    }

    @Test
    fun `does not call pv-data if sampled false`() {
        every { cm.spConfig } returns spConfig.copy(
            campaigns = listOf(SpCampaign(campaignType = CampaignType.USNAT))
        )
        every { ds.usnatSampled } returns false
        val service = ServiceImpl(ncMock, cm, cmu, ds, logger, MockExecutorManager(), connectionManager, coreCoordinator)
        service.pvData(messagesParamReq, onFailure = { _, _ -> })
        verify(exactly = 0) { ncMock.postPvData(any()) }
    }
}
