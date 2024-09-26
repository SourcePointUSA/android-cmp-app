package com.sourcepoint.cmplibrary.campaign

import com.sourcepoint.cmplibrary.* //ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.model.optimized.CcpaCS
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.serialization.json.JsonObject
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
        propertyId = 9090,
        messageTimeout = 3000,
    )

    private val spConfigWithUsnat = SpConfig(
        accountId = 22,
        propertyName = "carm.uw.con",
        messageLanguage = MessageLanguage.ENGLISH,
        propertyId = 9090,
        messageTimeout = 3000,
        campaigns = listOf(SpCampaign(CampaignType.USNAT), SpCampaign(CampaignType.GDPR))
    )

    private val spConfigWithoutUsnat = SpConfig(
        accountId = 22,
        propertyName = "carm.uw.con",
        messageLanguage = MessageLanguage.ENGLISH,
        propertyId = 9090,
        messageTimeout = 3000,
        campaigns = listOf(SpCampaign(CampaignType.GDPR))
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true, relaxed = true)
        every { gdprConsent.thisContent }.returns(JSONObject())
        sut.clearConsents()
    }

    private val sut by lazy { CampaignManager.create(dataStorage, spConfig) }

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
    fun `GIVEN a GDPR config RETURN the configuration with language EN`() {
        val sut = CampaignManager.create(
            dataStorage,
            spConfig.copy(messageLanguage = MessageLanguage.ENGLISH)
        )
        val config = sut.getPmConfig(CampaignType.GDPR, "11", PMTab.DEFAULT).getOrNull().assertNotNull()!!
        config.run {
            pmTab.assertEquals(PMTab.DEFAULT)
            consentLanguage.assertEquals("EN")
            uuid.assertEquals("")
            siteId.assertEquals("9090")
            messageId.assertEquals("11")
        }
    }

    @Test
    fun `GIVEN a GDPR config RETURN the configuration with language BG`() {
        val sut = CampaignManager.create(
            dataStorage,
            spConfig.copy(messageLanguage = MessageLanguage.BULGARIAN)
        )
        val config = sut.getPmConfig(CampaignType.GDPR, "22", PMTab.PURPOSES).getOrNull().assertNotNull()!!

        config.run {
            pmTab.assertEquals(PMTab.PURPOSES)
            consentLanguage.assertEquals("BG")
            uuid.assertEquals("")
            siteId.assertEquals("9090")
            messageId.assertEquals("22")
        }
    }

    @Test
    fun `GIVEN a GDPR config RETURN the configuration with language ES`() {

        every { dataStorage.gdprConsentUuid }.returns("uuid-test")

        val sut = CampaignManager.create(
            dataStorage,
            spConfig.copy(messageLanguage = MessageLanguage.SPANISH)
        )

        val config = sut.getPmConfig(
            campaignType = CampaignType.GDPR,
            pmId = "22",
            pmTab = PMTab.PURPOSES,
            useGroupPmIfAvailable = true,
            groupPmId = null
        ).getOrNull().assertNotNull()!!

        config.run {
            pmTab.assertEquals(PMTab.PURPOSES)
            consentLanguage.assertEquals("ES")
            uuid.assertEquals("uuid-test")
            siteId.assertEquals("9090")
            messageId.assertEquals("22")
        }
    }

    @Test
    fun `GIVEN a GDPR config RETURN the configuration with language NL and groupPmId not empty`() {

        every { dataStorage.gdprConsentUuid }.returns("uuid")
        every { dataStorage.gdprChildPmId }.returns("8989")

        val sut = CampaignManager.create(
            dataStorage,
            spConfig.copy(messageLanguage = MessageLanguage.DUTCH)
        )

        val config = sut.getPmConfig(
            campaignType = CampaignType.GDPR,
            pmId = "22",
            pmTab = PMTab.PURPOSES,
            useGroupPmIfAvailable = true,
            groupPmId = "111"
        ).getOrNull().assertNotNull()!!

        config.run {
            pmTab.assertEquals(PMTab.PURPOSES)
            consentLanguage.assertEquals("NL")
            uuid.assertEquals("uuid")
            siteId.assertEquals("9090")
            messageId.assertEquals("8989")
        }
    }

    /**
     * Test case which verifies that when ccpaConsentStatus is being set the value of uspstring is
     * being changed as well
     */
    @Test
    fun `ccpaConsentStatus - WHEN set THEN should update update uspstring value in data storage`() {

        // GIVEN
        val mockCcpaConsentStatus = CcpaCS(
            applies = true,
            consentedAll = true,
            dateCreated = "fake_date",
            gpcEnabled = false,
            newUser = false,
            rejectedAll = false,
            rejectedCategories = listOf(),
            rejectedVendors = listOf(),
            signedLspa = true,
            status = CcpaStatus.rejectedSome,
            uuid = "fake_uuid",
            webConsentPayload = JsonObject(mapOf()),
            expirationDate = null
        )

        // WHEN
        sut.ccpaConsentStatus = mockCcpaConsentStatus

        // THEN
        verify(atLeast = 1) { dataStorage.uspstring = mockCcpaConsentStatus.uspstring }
    }

    @Test
    fun `GIVEN an older usnat dataRecordedConsent compare to additionsChangeDate RETURN an updated USNatConsentStatus`() {

        every { dataStorage.usNatConsentData }.returns(usnatConsentData)
        every { dataStorage.metaDataResp }.returns(metaData)

        sut.reConsentUsnat(
            additionsChangeDate = "2023-11-07T15:21:00.414Z",
        )!!.apply {
            vendorListAdditions!!.assertTrue()
            consentedToAll!!.assertFalse()
            granularStatus!!.previousOptInAll!!.assertTrue()
        }
    }

    @Test
    fun `GIVEN an updated usnat consent RETURN a null object`() {

        every { dataStorage.usNatConsentData }.returns(usnatConsentData)
        every { dataStorage.metaDataResp }.returns(metaData)

        sut.reConsentUsnat(
            additionsChangeDate = "2021-11-07T15:21:00.414Z",
        ).assertNull()
    }

    @Test
    fun `GIVEN an older gdpr dataRecordedConsent compare to additionsChangeDate RETURN an updated GdprConsentStatus`() {

        every { dataStorage.gdprConsentStatus }.returns(gdprConsentStatus)
        every { dataStorage.metaDataResp }.returns(metaData)

        sut.reConsentGdpr(
            additionsChangeDate = "2023-11-07T15:21:00.414Z",
            legalBasisChangeDate = "2021-11-07T15:21:00.414Z",
        )!!.apply {
            vendorListAdditions!!.assertTrue()
            legalBasisChanges!!.assertFalse()
            consentedAll!!.assertFalse()
            granularStatus!!.previousOptInAll!!.assertTrue()
        }
    }

    @Test
    fun `GIVEN an updated gdpr consent RETURN a null object`() {

        every { dataStorage.gdprConsentStatus }.returns(gdprConsentStatus)
        every { dataStorage.metaDataResp }.returns(metaData)

        sut.reConsentGdpr(
            additionsChangeDate = "2021-11-07T15:21:00.414Z",
            legalBasisChangeDate = "2021-11-07T15:21:00.414Z",
        ).assertNull()
    }

    @Test
    fun `GIVEN an older gdpr dataRecordedConsent compare to legalBasisChangeDateConsentDate RETURN an updated GdprConsentStatus`() {

        every { dataStorage.gdprConsentStatus }.returns(gdprConsentStatus)
        every { dataStorage.metaDataResp }.returns(metaData)

        sut.reConsentGdpr(
            additionsChangeDate = "2021-11-07T15:21:00.414Z",
            legalBasisChangeDate = "2023-11-07T15:21:00.414Z",
        )!!.apply {
            vendorListAdditions!!.assertFalse()
            legalBasisChanges!!.assertTrue()
            consentedAll!!.assertFalse()
            granularStatus!!.previousOptInAll!!.assertTrue()
        }
    }

    @Test
    fun `GIVEN a dataRecordedConsent more recent compare to legalBasisChangeDateConsentDate and additionsChangeDate RETURN a null USNatConsentStatus`() {

        every { dataStorage.usNatConsentData }.returns(usnatConsentData)
        every { dataStorage.metaDataResp }.returns(metaData)

        sut.reConsentUsnat(
            additionsChangeDate = "2021-11-07T15:21:00.414Z",
        ).assertNull()
    }

    @Test
    fun `GIVEN a different applicableSections compared with the stored one RETURN true`() {
        every { dataStorage.metaDataResp }.returns(
            """
           {"usnat":{"applicableSections":[7]}} 
            """.trimIndent()
        )

        CampaignManager
            .create(dataStorage, spConfigWithUsnat)
            .hasUsnatApplicableSectionsChanged(createMetaDataResp(sections = listOf(8)).usnat)
            .assertTrue()
    }

    @Test
    fun `GIVEN an applicableSections without changes compared with the stored one RETURN false`() {
        every { dataStorage.metaDataResp }.returns(
            """
           {"usnat":{"applicableSections":[7]}} 
            """.trimIndent()
        )

        CampaignManager
            .create(dataStorage, spConfigWithUsnat)
            .hasUsnatApplicableSectionsChanged(createMetaDataResp(sections = listOf(8)).usnat)
            .assertTrue()
    }

    @Test
    fun `GIVEN response obj null RETURN false`() {

        every { dataStorage.metaDataResp }.returns(
            """
           {"usnat":{"applicableSections":[7]}} 
            """.trimIndent()
        )

        val response = null
        val sut = CampaignManager.create(dataStorage, spConfigWithUsnat)
        sut.hasUsnatApplicableSectionsChanged(response).assertFalse()
    }

    @Test
    fun `GIVEN metaDataResp obj null RETURN false`() {
        every { dataStorage.metaDataResp }.returns(null)

        CampaignManager
            .create(dataStorage, spConfigWithUsnat)
            .hasUsnatApplicableSectionsChanged(null)
            .assertFalse()
    }

    @Test
    fun `GIVEN an applicableSections without usnat configured RETURN false`() {
        every { dataStorage.metaDataResp }.returns(
            """
           {"usnat":{"applicableSections":[7]}} 
            """.trimIndent()
        )

        CampaignManager
            .create(dataStorage, spConfigWithUsnat)
            .hasUsnatApplicableSectionsChanged(createMetaDataResp(sections = listOf(7)).usnat)
            .assertFalse()
    }

    private fun createMetaDataResp(sections: List<Int>) = MetaDataResponse(
        usnat = MetaDataResponse.MetaDataResponseUSNat(
            vendorListId = "",
            applies = true,
            sampleRate = 1.0f,
            additionsChangeDate = "",
            applicableSections = sections,
        ),
        ccpa = null,
        gdpr = null
    )

    private val usnatConsentData = """
        {
          "applies": true,
          "categories": [
            "65549686a25ae9584d43de1a",
            "65549686a25ae9584d43de2c",
            "65549686a25ae9584d43de3e",
            "65549686a25ae9584d43de50",
            "65549686a25ae9584d43de62",
            "65549686a25ae9584d43de74",
            "65549686a25ae9584d43de86",
            "65549686a25ae9584d43de98",
            "65549686a25ae9584d43deaa",
            "65549686a25ae9584d43debc",
            "65549686a25ae9584d43dece",
            "65549686a25ae9584d43dee0"
          ],
          "consentStatus": {
            "rejectedAny": false,
            "consentedToAll": true,
            "consentedToAny": true,
            "legalBasisChanges": false,
            "vendorListAdditions": false,
            "granularStatus": {
              "sellStatus": false,
              "shareStatus": false,
              "sensitiveDataStatus": true,
              "gpcStatus": false
            },
            "hasConsentData": true
          },
          "consentString": "BAAAAAAAAqA",
          "dateCreated": "2022-11-16T12:22:31.180Z",
          "uuid": "dea86cfe-3551-4507-a25f-7f93d386deaf_7",
          "webConsentPayload": {
            "actions": [
            ],
            "categories": [
              "65549686a25ae9584d43de1a",
              "65549686a25ae9584d43de2c",
              "65549686a25ae9584d43de3e",
              "65549686a25ae9584d43de50",
              "65549686a25ae9584d43de62",
              "65549686a25ae9584d43de74",
              "65549686a25ae9584d43de86",
              "65549686a25ae9584d43de98",
              "65549686a25ae9584d43deaa",
              "65549686a25ae9584d43debc",
              "65549686a25ae9584d43dece",
              "65549686a25ae9584d43dee0"
            ],
            "consentStatus": {
              "rejectedAny": false,
              "consentedToAll": true,
              "consentedToAny": true,
              "granularStatus": {
                "sellStatus": false,
                "shareStatus": false,
                "sensitiveDataStatus": true,
                "gpcStatus": false
              },
              "hasConsentData": true
            },
            "consentString": "BAAAAAAAAqA",
            "cookies": [
            ],
            "dateCreated": "2022-11-16T12:22:31.180Z",
            "userConsents": {
              "categories": [
              ]
            },
            "uuid": "dea86cfe-3551-4507-a25f-7f93d386deaf_7"
          },
          "type": "USNAT",
          "expirationDate": "2024-11-15T12:22:31.180Z"
        }
    """.trimIndent()

    private val metaData = """
        {
          "gdpr": {
            "additionsChangeDate": "2023-11-20T09:30:31.799Z",
            "getMessageAlways": false,
            "legalBasisChangeDate": "2023-11-20T09:30:31.799Z",
            "sample": false,
            "version": 3,
            "_id": "655b27374a917370b249a721",
            "applies": true,
            "sampleRate": 1
          },
          "usnat": {
            "_id": "655b1e1ae17a3c2fad348151",
            "additionsChangeDate": "2023-11-20T13:40:58.865Z",
            "applicableSections": [
              7
            ],
            "applies": true,
            "sample": false,
            "version": 4,
            "sampleRate": 1
          }
        }
    """.trimIndent()

    private val gdprConsentStatus = """
        {
          "applies": true,
          "categories": [
            "655b27377cf56b03be566e74",
            "655b27377cf56b03be566e7c",
            "655b27377cf56b03be566e84",
            "655b27377cf56b03be566e8b",
            "655b27377cf56b03be566e92",
            "655b27377cf56b03be566e98"
          ],
          "consentAllRef": "656c99934a917321cc0e2d3d",
          "consentedToAll": true,
          "legIntCategories": [
            "655b27377cf56b03be566e7c"
          ],
          "legIntVendors": [
            "5fda5252b4f7ae27b97a1dfe",
            "5f3a3e66ee0a81887437291c"
          ],
          "postPayload": {
            "consentAllRef": "656c99934a917321cc0e2d3d",
            "granularStatus": {
              "defaultConsent": false,
              "previousOptInAll": false,
              "purposeConsent": "ALL",
              "purposeLegInt": "ALL",
              "vendorConsent": "ALL",
              "vendorLegInt": "ALL"
            },
            "vendorListId": "655b27374a917370b249a721"
          },
          "rejectedAny": false,
          "specialFeatures": [],
          "vendors": [
            "5ff4d000a228633ac048be41",
            "5f3a3e66ee0a81887437291c",
            "5fda5252b4f7ae27b97a1dfe"
          ],
          "addtlConsent": "1~",
          "consentStatus": {
            "consentedAll": true,
            "consentedToAny": true,
            "granularStatus": {
              "defaultConsent": false,
              "previousOptInAll": false,
              "purposeConsent": "ALL",
              "purposeLegInt": "ALL",
              "vendorConsent": "ALL",
              "vendorLegInt": "ALL"
            },
            "legalBasisChanges": false,
            "vendorListAdditions": false,
            "hasConsentData": true,
            "rejectedAny": false,
            "rejectedLI": false
          },
          "customVendorsResponse": {
            "consentedPurposes": [
              {
                "_id": "655b27377cf56b03be566e74",
                "name": "Store and/or access information on a device"
              },
              {
                "_id": "655b27377cf56b03be566e7c",
                "name": "Use limited data to select advertising"
              },
              {
                "_id": "655b27377cf56b03be566e84",
                "name": "Create profiles for personalised advertising"
              },
              {
                "_id": "655b27377cf56b03be566e8b",
                "name": "Use profiles to select personalised advertising"
              },
              {
                "_id": "655b27377cf56b03be566e92",
                "name": "Create profiles to personalise content"
              },
              {
                "_id": "655b27377cf56b03be566e98",
                "name": "Use profiles to select personalised content"
              }
            ],
            "consentedVendors": [
              {
                "_id": "5ff4d000a228633ac048be41",
                "name": "Game Accounts",
                "vendorType": "CUSTOM"
              },
              {
                "_id": "5fda5252b4f7ae27b97a1dfe",
                "name": "Google Charts",
                "vendorType": "CUSTOM"
              },
              {
                "_id": "5f3a3e66ee0a81887437291c",
                "name": "Trivago",
                "vendorType": "CUSTOM"
              }
            ],
            "legIntPurposes": [
              {
                "_id": "655b27377cf56b03be566e7c",
                "name": "Use limited data to select advertising"
              }
            ]
          },
          "dateCreated": "2022-11-16T12:22:31.180Z",
          "euconsent": "CP2NbIAP2NbIAAGABCENAdEgAPwAAEAAAAYgAAAAAAAA.YAAAAAAAAAAA",
          "grants": {
            "5ff4d000a228633ac048be41": {
              "vendorGrant": true,
              "purposeGrants": {
                "655b27377cf56b03be566e74": true,
                "655b27377cf56b03be566e7c": true,
                "655b27377cf56b03be566e84": true,
                "655b27377cf56b03be566e8b": true
              }
            },
            "5f3a3e66ee0a81887437291c": {
              "vendorGrant": true,
              "purposeGrants": {
                "655b27377cf56b03be566e74": true,
                "655b27377cf56b03be566e7c": true,
                "655b27377cf56b03be566e8b": true
              }
            },
            "5fda5252b4f7ae27b97a1dfe": {
              "vendorGrant": true,
              "purposeGrants": {
                "655b27377cf56b03be566e74": true,
                "655b27377cf56b03be566e7c": true,
                "655b27377cf56b03be566e84": true,
                "655b27377cf56b03be566e92": true,
                "655b27377cf56b03be566e98": true
              }
            }
          },
          "TCData": {
            "IABTCF_AddtlConsent": "1~",
            "IABTCF_CmpSdkID": 6,
            "IABTCF_CmpSdkVersion": 2,
            "IABTCF_PolicyVersion": 4,
            "IABTCF_PublisherCC": "DE",
            "IABTCF_PurposeOneTreatment": 0,
            "IABTCF_UseNonStandardTexts": 0,
            "IABTCF_TCString": "CP2NbIAP2NbIAAGABCENAdEgAPwAAEAAAAYgAAAAAAAA.YAAAAAAAAAAA",
            "IABTCF_VendorConsents": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "IABTCF_VendorLegitimateInterests": "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000",
            "IABTCF_PurposeConsents": "1111110000",
            "IABTCF_PurposeLegitimateInterests": "0100000000",
            "IABTCF_SpecialFeaturesOptIns": "00",
            "IABTCF_PublisherConsent": "00000000000",
            "IABTCF_PublisherLegitimateInterests": "00000000000",
            "IABTCF_PublisherCustomPurposesConsents": "00000000000",
            "IABTCF_PublisherCustomPurposesLegitimateInterests": "00000000000",
            "IABTCF_gdprApplies": 1
          },
          "uuid": "0a9a7b2d-ddb7-467f-9e3e-da53860b5ac4",
          "vendorListId": "655b27374a917370b249a721",
          "webConsentPayload": {
            "actions": [],
            "addtlConsent": "1~",
            "cookies": [],
            "consentStatus": {
              "rejectedAny": false,
              "rejectedLI": false,
              "consentedAll": true,
              "granularStatus": {
                "vendorConsent": "ALL",
                "vendorLegInt": "ALL",
                "purposeConsent": "ALL",
                "purposeLegInt": "ALL",
                "previousOptInAll": false,
                "defaultConsent": false
              },
              "hasConsentData": true,
              "consentedToAny": true
            },
            "customVendorsResponse": {
              "consentedVendors": [
                {
                  "_id": "5ff4d000a228633ac048be41",
                  "name": "Game Accounts",
                  "vendorType": "CUSTOM"
                },
                {
                  "_id": "5fda5252b4f7ae27b97a1dfe",
                  "name": "Google Charts",
                  "vendorType": "CUSTOM"
                },
                {
                  "_id": "5f3a3e66ee0a81887437291c",
                  "name": "Trivago",
                  "vendorType": "CUSTOM"
                }
              ],
              "consentedPurposes": [
                {
                  "_id": "655b27377cf56b03be566e74",
                  "name": "Store and/or access information on a device"
                },
                {
                  "_id": "655b27377cf56b03be566e7c",
                  "name": "Use limited data to select advertising"
                },
                {
                  "_id": "655b27377cf56b03be566e84",
                  "name": "Create profiles for personalised advertising"
                },
                {
                  "_id": "655b27377cf56b03be566e8b",
                  "name": "Use profiles to select personalised advertising"
                },
                {
                  "_id": "655b27377cf56b03be566e92",
                  "name": "Create profiles to personalise content"
                },
                {
                  "_id": "655b27377cf56b03be566e98",
                  "name": "Use profiles to select personalised content"
                }
              ],
              "legIntPurposes": [
                {
                  "_id": "655b27377cf56b03be566e7c",
                  "name": "Use limited data to select advertising"
                }
              ]
            },
            "dateCreated": "2022-11-16T12:22:31.180Z",
            "expirationDate": "2024-12-02T15:06:59.325Z",
            "euconsent": "CP2NbIAP2NbIAAGABCENAdEgAPwAAEAAAAYgAAAAAAAA.YAAAAAAAAAAA",
            "gdprApplies": true,
            "grants": {
              "5ff4d000a228633ac048be41": {
                "vendorGrant": true,
                "purposeGrants": {
                  "655b27377cf56b03be566e74": true,
                  "655b27377cf56b03be566e7c": true,
                  "655b27377cf56b03be566e84": true,
                  "655b27377cf56b03be566e8b": true
                }
              },
              "5f3a3e66ee0a81887437291c": {
                "vendorGrant": true,
                "purposeGrants": {
                  "655b27377cf56b03be566e74": true,
                  "655b27377cf56b03be566e7c": true,
                  "655b27377cf56b03be566e8b": true
                }
              },
              "5fda5252b4f7ae27b97a1dfe": {
                "vendorGrant": true,
                "purposeGrants": {
                  "655b27377cf56b03be566e74": true,
                  "655b27377cf56b03be566e7c": true,
                  "655b27377cf56b03be566e84": true,
                  "655b27377cf56b03be566e92": true,
                  "655b27377cf56b03be566e98": true
                }
              }
            },
            "vendorListId": "655b27374a917370b249a721"
          },
          "expirationDate": "2024-12-02T15:06:59.325Z"
        }
    """.trimIndent()
}
