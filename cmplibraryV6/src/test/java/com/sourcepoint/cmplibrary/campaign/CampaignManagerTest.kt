package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.CCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.data.network.model.GDPRConsent
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
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
    private lateinit var gdpr: GDPRConsent

    @MockK
    private lateinit var ccpa: CCPAConsent

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        every { gdpr.thisContent }.returns(JSONObject())
        sut.clearConsents()
    }

    private val sut by lazy { CampaignManager.create(dataStorage) }

    @Test
    fun `GIVEN a GDPRConsent CHECK that is properly stored`() {

        sut.saveGDPRConsent(gdpr)

        verify(exactly = 1) { dataStorage.saveGdprConsentResp(any()) }
        verify(exactly = 0) { dataStorage.getGdprConsentResp() }
    }

    @Test
    fun `CHECK that getGDPRConsent RETURNS a GDPRConsent from cache`() {

        sut.saveGDPRConsent(gdpr)
        (sut.getGDPRConsent() as? Either.Right)
            .assertNotNull()
            .let { it!!.r }
            .assertEquals(gdpr)

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

        sut.saveCCPAConsent(ccpa)

        verify(exactly = 1) { dataStorage.saveCcpaConsentResp(any()) }
        verify(exactly = 0) { dataStorage.getCcpaConsentResp() }
    }

    @Test
    fun `CHECK that getCCPAConsent RETURNS a CCPAConsent from cache`() {

        sut.saveCCPAConsent(ccpa)
        (sut.getCCPAConsent() as? Either.Right)
            .assertNotNull()
            .let { it!!.r }
            .assertEquals(ccpa)

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
            saveCCPAConsent(ccpa)
            saveGDPRConsent(gdpr)
        }
        val sut2 = CampaignManager.create(dataStorage).apply {
            saveCCPAConsent(ccpa)
            saveGDPRConsent(gdpr)
        }

        (sut1.getGDPRConsent() as Either.Right).r.assertEquals((sut2.getGDPRConsent() as Either.Right).r)
        (sut1.getCCPAConsent() as Either.Right).r.assertEquals((sut2.getCCPAConsent() as Either.Right).r)

        verify(exactly = 0) { dataStorage.getCcpaConsentResp() }
    }
}
