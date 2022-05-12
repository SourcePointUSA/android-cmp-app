package com.sourcepoint.cmplibrary

import android.content.Context
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.consent.ClientEventManager
import com.sourcepoint.cmplibrary.consent.ConsentManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.ExecutorManager
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.util.HttpUrlManager
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.util.ViewsManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SpConsentLibImplTest {

    internal var campaign = Campaign(22, "tcfv2.mobile.webview", "122058")

    @MockK
    private lateinit var appCtx: Context

    @MockK
    private lateinit var logger: Logger

    @MockK
    private lateinit var clientEventManager: ClientEventManager

    @MockK
    private lateinit var jsonConverter: JsonConverter

    @MockK
    private lateinit var connManager: ConnectionManager

    @MockK
    private lateinit var dataStorage: DataStorage

    @MockK
    private lateinit var viewManager: ViewsManager

    @MockK
    private lateinit var campaignManager: CampaignManager

    @MockK
    private lateinit var consentManager: ConsentManager

    @MockK
    private lateinit var consentManagerUtils: ConsentManagerUtils

    @MockK
    private lateinit var execManager: ExecutorManager

    @MockK
    private lateinit var spClient: SpClient

    @MockK
    private lateinit var urlManager: HttpUrlManager

    @MockK
    private lateinit var service: Service

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, campaignType) RETURN the right params`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", GDPR)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.DEFAULT,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), "false") }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType) RETURN the right params`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), "false") }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType, useGroupPmIfAvailable) RETURN the right params`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR, true)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = true,
                groupPmId = null
            )
        }

        verify { logger.i(any(), "false") }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, campaignType) RETURN the right params for OTT`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)
        every { campaignManager.isCampaignOtt(any()) }.returns(true)

        val sut = createLib()
        sut.loadPrivacyManager("1234", GDPR)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.DEFAULT,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), "true") }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType) RETURN the right params for OTT`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)
        every { campaignManager.isCampaignOtt(any()) }.returns(true)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), "true") }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType, useGroupPmIfAvailable) RETURN the right params for OTT`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)
        every { campaignManager.isCampaignOtt(any()) }.returns(true)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR, true)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = true,
                groupPmId = null
            )
        }

        verify { logger.i(any(), "true") }
    }

    @Test
    fun `CALLING loadMessage() verify that getMessage is called exactly 1 time`() {
        val sut = createLib()
        sut.loadMessage()
        verify(exactly = 1) { service.getUnifiedMessage(any(), any(), any(), any()) }
    }

    @Test
    fun `CALLING loadMessage() with verify that spClient is called`() = runBlocking<Unit> {
//        val mr = MessageResp(legislation = Legislation.GDPR, message = JSONObject(), uuid = "", meta = "", userConsent = mockk())
//        val mockService = MockService(
//            getMessageLogic = { _, pSuccess, _ -> pSuccess.invoke(mr) }
//        )
//        val sut = ConsentLibImpl(urlManager, campaign, PrivacyManagerTabK.FEATURES, appCtx, logger, jsonConverter, connManager, mockService, viewManager, execManager)
//        sut.spClient = spClient
//
//        sut.loadMessage()

//        val slot = slot<ActionTypes>()
//        verify(exactly = 1) { spClient.onAction(capture(slot)) }
//        slot.captured
    }

    internal fun createLib() = SpConsentLibImpl(
        context = appCtx,
        pLogger = logger,
        pJsonConverter = jsonConverter,
        service = service,
        executor = execManager,
        viewManager = viewManager,
        campaignManager = campaignManager,
        consentManager = consentManager,
        urlManager = urlManager,
        dataStorage = dataStorage,
        spClient = spClient,
        clientEventManager = clientEventManager
    )
}
