package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignTemplate
import com.sourcepoint.cmplibrary.model.Ccpa
import com.sourcepoint.cmplibrary.model.Gdpr
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.file2String
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class CampaignManagerTest {

    @MockK
    private lateinit var dataStorage: DataStorage

    @MockK
    private lateinit var gdprConsent: GDPRConsentInternal

    @MockK
    private lateinit var ccpaConsent: CCPAConsentInternal

    @MockK
    private lateinit var gdpr: CampaignTemplate

    @MockK
    private lateinit var ccpa: CampaignTemplate

    private val gdprCampaign = SpCampaign(
        CampaignType.GDPR,
        listOf(TargetingParam("location", "EU"))
    )

    private val ccpaCamapign = SpCampaign(
        CampaignType.CCPA,
        listOf(TargetingParam("location", "EU"))
    )

    private val spConfig = SpConfig(
        22,
        "carm.uw.con",
        listOf(
            ccpaCamapign,
            gdprCampaign
        ),
        MessageLanguage.ENGLISH,
        3000
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        every { gdprConsent.thisContent }.returns(JSONObject())
        sut.clearConsents()
    }

    private val sut by lazy { CampaignManager.create(dataStorage, spConfig, MessageLanguage.ENGLISH) }

    @Test
    fun `CHECK that getGDPRConsent RETURNS a GDPRConsent from the dataStorage`() {
        val unifiedResp = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()
        val gdprTest = unifiedResp.campaigns.find { it is Gdpr } as Gdpr

        every { dataStorage.getGdprConsentResp() }.returns(gdprTest.userConsent.thisContent.toString())

        (sut.getGDPRConsent() as? Either.Right).assertNotNull()

        verify(exactly = 0) { dataStorage.saveGdprConsentResp(any()) }
    }

    @Test
    fun `CHECK that getCCPAConsent RETURNS a CCPAConsent from the dataStorage`() {
        val unifiedResp = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()
        val ccpaTest = unifiedResp.campaigns.find { it is Ccpa } as Ccpa

        every { dataStorage.getCcpaConsentResp() }.returns(ccpaTest.userConsent.thisContent.toString())

        (sut.getCCPAConsent() as? Either.Right).assertNotNull()

        verify(exactly = 1) { dataStorage.getCcpaConsentResp() }
        verify(exactly = 0) { dataStorage.saveCcpaConsentResp(any()) }
    }

    @Test
    fun `GIVEN a gdpr applied campaign RETURN a gdpr type`() {
        every { dataStorage.getGdprMessage() }.returns("GDPR")
        sut.run {
            addCampaign(CampaignType.CCPA, ccpa)
            addCampaign(CampaignType.GDPR, gdpr)
        }
        val pairOutput = sut.getAppliedCampaign()

        (pairOutput as Either.Right).r.first.assertEquals(CampaignType.GDPR)
    }

    @Test
    fun `GIVEN a ccpa applied campaign RETURN a ccpa type`() {
        every { dataStorage.getCcpaMessage() }.returns("CCPA")
        sut.run {
            addCampaign(CampaignType.CCPA, ccpa)
            addCampaign(CampaignType.GDPR, gdpr)
        }
        val pairOutput = sut.getAppliedCampaign()

        (pairOutput as Either.Right).r.first.assertEquals(CampaignType.CCPA)
    }

    @Test
    fun `GIVEN a gdpr applied campaign RETURN true passing GPDR as parameter`() {
        every { dataStorage.getGdprMessage() }.returns("GDPR")
        sut.run {
            addCampaign(CampaignType.CCPA, ccpa)
            addCampaign(CampaignType.GDPR, gdpr)
        }
        sut.isAppliedCampaign(CampaignType.GDPR).assertTrue()
        sut.isAppliedCampaign(CampaignType.CCPA).assertFalse()
    }

    @Test
    fun `GIVEN a ccpa applied campaign RETURN true passing CCPA as parameter`() {
        every { dataStorage.getCcpaMessage() }.returns("CCPA")
        sut.run {
            addCampaign(CampaignType.CCPA, ccpa)
            addCampaign(CampaignType.GDPR, gdpr)
        }
        sut.isAppliedCampaign(CampaignType.CCPA).assertTrue()
        sut.isAppliedCampaign(CampaignType.GDPR).assertFalse()
    }
}
