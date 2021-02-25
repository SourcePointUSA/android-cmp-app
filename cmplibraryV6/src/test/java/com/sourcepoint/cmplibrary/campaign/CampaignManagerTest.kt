package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.CCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.data.network.model.GDPRConsent
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CCPACampaign
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import com.sourcepoint.cmplibrary.util.Either
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
    private lateinit var gdprConsent: GDPRConsent

    @MockK
    private lateinit var ccpaConsent: CCPAConsent

    @MockK
    private lateinit var gdpr: GDPRCampaign

    @MockK
    private lateinit var ccpa: CCPACampaign

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        every { gdprConsent.thisContent }.returns(JSONObject())
        sut.clearConsents()
    }

    private val sut by lazy { CampaignManager.create(dataStorage) }

    @Test
    fun `GIVEN a GDPRConsent CHECK that is properly stored`() {

        sut.saveGDPRConsent(gdprConsent)

        verify(exactly = 1) { dataStorage.saveGdprConsentResp(any()) }
        verify(exactly = 0) { dataStorage.getGdprConsentResp() }
    }

    @Test
    fun `CHECK that getGDPRConsent RETURNS a GDPRConsent from cache`() {

        sut.saveGDPRConsent(gdprConsent)
        (sut.getGDPRConsent() as? Either.Right)
            .assertNotNull()
            .let { it!!.r }
            .assertEquals(gdprConsent)

        verify(exactly = 1) { dataStorage.saveGdprConsentResp(any()) }
        verify(exactly = 0) { dataStorage.getGdprConsentResp() }
    }

    @Test
    fun `CHECK that getGDPRConsent RETURNS a GDPRConsent from the dataStorage`() {
        val unifiedResp = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()
        val gdprTest = unifiedResp.campaigns.find { it is Gdpr } as Gdpr

        every { dataStorage.getGdprConsentResp() }.returns(gdprTest.userConsent!!.thisContent.toString())

        (sut.getGDPRConsent() as? Either.Right).assertNotNull()

        verify(exactly = 0) { dataStorage.saveGdprConsentResp(any()) }
        verify(exactly = 1) { dataStorage.getGdprConsentResp() }
    }

    @Test
    fun `GIVEN a CCPAConsent CHECK that is properly stored`() {

        sut.saveCCPAConsent(ccpaConsent)

        verify(exactly = 1) { dataStorage.saveCcpaConsentResp(any()) }
        verify(exactly = 0) { dataStorage.getCcpaConsentResp() }
    }

    @Test
    fun `CHECK that getCCPAConsent RETURNS a CCPAConsent from cache`() {

        sut.saveCCPAConsent(ccpaConsent)
        (sut.getCCPAConsent() as? Either.Right)
            .assertNotNull()
            .let { it!!.r }
            .assertEquals(ccpaConsent)

        verify(exactly = 1) { dataStorage.saveCcpaConsentResp(any()) }
        verify(exactly = 0) { dataStorage.getCcpaConsentResp() }
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
    fun `VERIFY that getCCPAConsent AND getGDPRConsent RETUN static objects`() {

        val sut1 = CampaignManager.create(dataStorage).apply {
            saveCCPAConsent(ccpaConsent)
            saveGDPRConsent(gdprConsent)
        }
        val sut2 = CampaignManager.create(dataStorage).apply {
            saveCCPAConsent(ccpaConsent)
            saveGDPRConsent(gdprConsent)
        }

        (sut1.getGDPRConsent() as Either.Right).r.assertEquals((sut2.getGDPRConsent() as Either.Right).r)
        (sut1.getCCPAConsent() as Either.Right).r.assertEquals((sut2.getCCPAConsent() as Either.Right).r)

        verify(exactly = 0) { dataStorage.getCcpaConsentResp() }
    }

    @Test
    fun `VERIFY that getCCPAConsent AND getGDPRConsent GETS cleared with null values`() {

        sut.saveCCPAConsent(null)
        sut.saveGDPRConsent(null)

        (sut.getCCPAConsent() as? Either.Left).assertNotNull()
        (sut.getGDPRConsent() as? Either.Left).assertNotNull()

        verify(exactly = 1) { dataStorage.getGdprConsentResp() }
        verify(exactly = 1) { dataStorage.getCcpaConsentResp() }
    }

    @Test
    fun `VERIFY that getCCPAConsent AND getGDPRConsent GETS cleared calling clearConsents`() {

        sut.run {
            saveCCPAConsent(ccpaConsent)
            saveGDPRConsent(gdprConsent)
        }

        sut.clearConsents()

        (sut.getCCPAConsent() as? Either.Left).assertNotNull()
        (sut.getGDPRConsent() as? Either.Left).assertNotNull()

        verify(exactly = 1) { dataStorage.getGdprConsentResp() }
        verify(exactly = 1) { dataStorage.getCcpaConsentResp() }
    }

    @Test
    fun `GIVEN a gdpr applied campaign RETURN a gdpr type`() {
        every { dataStorage.getGdprMessage() }.returns("GDPR")
        sut.run {
            addCampaign(Legislation.CCPA, ccpa)
            addCampaign(Legislation.GDPR, gdpr)
        }
        val pairOutput = sut.getAppliedCampaign()

        (pairOutput as Either.Right).r.first.assertEquals(Legislation.GDPR)
    }

    @Test
    fun `GIVEN a ccpa applied campaign RETURN a ccpa type`() {
        every { dataStorage.getCcpaMessage() }.returns("CCPA")
        sut.run {
            addCampaign(Legislation.CCPA, ccpa)
            addCampaign(Legislation.GDPR, gdpr)
        }
        val pairOutput = sut.getAppliedCampaign()

        (pairOutput as Either.Right).r.first.assertEquals(Legislation.CCPA)
    }

    @Test
    fun `GIVEN a gdpr applied campaign RETURN true passing GPDR as parameter`() {
        every { dataStorage.getGdprMessage() }.returns("GDPR")
        sut.run {
            addCampaign(Legislation.CCPA, ccpa)
            addCampaign(Legislation.GDPR, gdpr)
        }
        sut.isAppliedCampaign(Legislation.GDPR).assertTrue()
        sut.isAppliedCampaign(Legislation.CCPA).assertFalse()
    }

    @Test
    fun `GIVEN a ccpa applied campaign RETURN true passing CCPA as parameter`() {
        every { dataStorage.getCcpaMessage() }.returns("CCPA")
        sut.run {
            addCampaign(Legislation.CCPA, ccpa)
            addCampaign(Legislation.GDPR, gdpr)
        }
        sut.isAppliedCampaign(Legislation.CCPA).assertTrue()
        sut.isAppliedCampaign(Legislation.GDPR).assertFalse()
    }
}
