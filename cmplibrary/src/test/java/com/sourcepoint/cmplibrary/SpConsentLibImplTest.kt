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
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory.OTT
import com.sourcepoint.cmplibrary.model.exposed.MessageType
import com.sourcepoint.cmplibrary.model.exposed.MessageType.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.ViewsManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
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

        every { connManager.isConnected } returns true
    }

    @Test
    fun `loadPrivacyManager - WHEN called with no Internet THEN should not proceed with the flow`() {
        // GIVEN
        val mockIsConnected = false
        every { connManager.isConnected } returns mockIsConnected

        // WHEN
        val sut = createLib()
        sut.loadPrivacyManager("1234", GDPR)

        // THEN
        verify(exactly = 0) { clientEventManager.setCampaignsToProcess(any()) }
        verify(exactly = 0) {
            campaignManager.getPmConfig(
                campaignType = any(),
                pmId = any(),
                pmTab = any(),
                useGroupPmIfAvailable = any(),
                groupPmId = any(),
            )
        }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, campaignType) VERIFY that getPmConfig receive the right params`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", GDPR, LEGACY_OTT)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.DEFAULT,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), LEGACY_OTT.name) }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType) VERIFY that getPmConfig receive the right params`() {

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

        verify { logger.i(any(), MOBILE.name) }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType, useGroupPmIfAvailable) VERIFY that getPmConfig receive the right params`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR, true, MessageType.OTT)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = true,
                groupPmId = null
            )
        }

        verify { logger.i(any(), OTT.name) }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, campaignType) VERIFY that getPmConfig receive the right params for OTT`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", GDPR, LEGACY_OTT)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.DEFAULT,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), LEGACY_OTT.name) }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType) VERIFY that getPmConfig receive the right params for OTT`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR, LEGACY_OTT)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = false,
                groupPmId = null
            )
        }

        verify { logger.i(any(), LEGACY_OTT.name) }
    }

    @Test
    fun `CALLING loadPrivacyManager(pmId, pmTab, campaignType, useGroupPmIfAvailable) VERIFY that getPmConfig receive the right params for OTT`() {

        every { campaignManager.getPmConfig(any(), any(), any(), any(), any()) }.returns(Either.Left(RuntimeException()))
        every { campaignManager.getGroupId(any()) }.returns(null)

        val sut = createLib()
        sut.loadPrivacyManager("1234", PMTab.VENDORS, GDPR, true, LEGACY_OTT)

        verify {
            campaignManager.getPmConfig(
                campaignType = GDPR,
                pmId = "1234",
                pmTab = PMTab.VENDORS,
                useGroupPmIfAvailable = true,
                groupPmId = null
            )
        }

        verify { logger.i(any(), LEGACY_OTT.name) }
    }

    private fun createLib() = SpConsentLibImpl(
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
        clientEventManager = clientEventManager,
        connectionManager = connManager,
    )
}
