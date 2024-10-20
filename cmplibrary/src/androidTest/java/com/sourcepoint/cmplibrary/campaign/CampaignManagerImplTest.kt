package com.sourcepoint.cmplibrary.campaign

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.* //ktlint-disable
import com.sourcepoint.cmplibrary.Utils.Companion.spEntries
import com.sourcepoint.cmplibrary.Utils.Companion.storeTestDataObj
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentData
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.util.file2String
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class CampaignManagerImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    private val gdpr = CampaignTemplate(
        campaignsEnv = CampaignsEnv.STAGE,
        targetingParams = listOf(TargetingParam("location", "EU")),
        campaignType = CampaignType.GDPR,
        groupPmId = "111" // 111
    )

    private val ccpa = CampaignTemplate(
        campaignsEnv = CampaignsEnv.STAGE,
        targetingParams = listOf(TargetingParam("location", "EU")),
        campaignType = CampaignType.CCPA,
        groupPmId = "111"
    )

    private val dataStorage by lazy {
        val gdprDs = DataStorageGdpr.create(appContext)
        val ccpaDs = DataStorageCcpa.create(appContext)
        val usNatDs = DataStorageUSNat.create(appContext)
        DataStorage.create(appContext, gdprDs, ccpaDs, usNatDs)
    }

    private val gdprCampaign = SpCampaign(
        CampaignType.GDPR,
        listOf(
            TargetingParam("location", "EU")
        )
    )

    private val ccpaCampaign = SpCampaign(
        CampaignType.CCPA,
        listOf(TargetingParam("location", "EU"))
    )

    private val spConfig = SpConfig(
        22,
        "carm.uw.con",
        listOf(
            ccpaCampaign,
            gdprCampaign
        ),
        MessageLanguage.ENGLISH,
        propertyId = 1234,
        messageTimeout = 3000,
    )

    private val campaignManager by lazy {
        CampaignManager.create(dataStorage, spConfig)
    }

    private val usnatMock by lazy {
        USNatConsentData(
            uuid = null,
            consentStatus = null,
            type = CampaignType.USNAT,
            url = null,
            messageMetaData = null,
            message = null,
            applies = null,
            expirationDate = null,
            dateCreated = null,
            consentStrings = null,
            gppData = null,
            webConsentPayload = null,
            userConsents = USNatConsentData.UserConsents(),
        )
    }

    @Before
    fun setup() {
        dataStorage.clearAll()
    }

    @Test
    fun given_a_groupPmId_RETURN_the_pmId() {

        val sut = CampaignManager.create(dataStorage, spConfig).apply {
            addCampaign(CampaignType.GDPR, gdpr)
            addCampaign(CampaignType.CCPA, ccpa)
        }

        sut
            .getPmConfig(CampaignType.GDPR, "11", PMTab.PURPOSES, false, "55")
            .getOrNull()!!
            .messageId
            .assertEquals("11")

        sut
            .getPmConfig(CampaignType.CCPA, "22", PMTab.PURPOSES, false, "55")
            .getOrNull()!!
            .messageId
            .assertEquals("22")

        // set the groupPmId

        dataStorage.gdprChildPmId = "33"
        dataStorage.ccpaChildPmId = "44"

        sut
            .getPmConfig(CampaignType.GDPR, "11", PMTab.PURPOSES, true, "33")
            .getOrNull()!!
            .messageId
            .assertEquals("33")
        sut
            .getPmConfig(CampaignType.CCPA, "22", PMTab.PURPOSES, true, "44")
            .getOrNull()!!
            .messageId
            .assertEquals("44")

        sut
            .getPmConfig(CampaignType.GDPR, "11", PMTab.PURPOSES, false, "33")
            .getOrNull()!!
            .messageId
            .assertEquals("11")
        sut
            .getPmConfig(CampaignType.CCPA, "22", PMTab.PURPOSES, false, "44")
            .getOrNull()!!
            .messageId
            .assertEquals("44") // it is 44 because the feature is not yet implemented on ccpa
    }

    @Test
    fun given_an_expired_GDPR_and_a_valid_CCPA_campaign_DELETE_only_the_GDPR_consent() {
        val json = JSONObject("v7/expired_gdpr_valid_ccpa.json".file2String())
        appContext.storeTestDataObj(json.toList())
        dataStorage.deleteGdprConsent()
        appContext.spEntries().toList().find { it.first.contains("GDPR") }.assertNull()
    }

    @Test
    fun given_an_expired_CCPA_and_a_valid_GDPR_campaign_DELETE_only_the_GDPR_consent() {
        val json = JSONObject("v7/expired_ccpa_valid_gdpr.json".file2String())
        appContext.storeTestDataObj(json.toList())
        dataStorage.deleteCcpaConsent()
        appContext.spEntries().toList().find { it.first.contains("CCPA") }.assertNull()
    }

    @Test
    fun given_an_expired_GDPR_isGdprExpired_RETURN_true() {

        dataStorage.gdprExpirationDate = "2022-10-27T17:15:57.006Z"
        dataStorage.ccpaExpirationDate = "2024-10-27T17:15:57.006Z"
        campaignManager.usNatConsentData = usnatMock.copy(expirationDate = "2024-10-27T17:15:57.006Z")

        campaignManager.isGdprExpired.assertTrue()
        campaignManager.isCcpaExpired.assertFalse()
        campaignManager.isUsnatExpired.assertFalse()
    }

    @Test
    fun given_an_expired_CCPA_isCcpaExpired_RETURN_true() {

        dataStorage.gdprExpirationDate = "2024-10-27T17:15:57.006Z"
        dataStorage.ccpaExpirationDate = "2022-10-27T17:15:57.006Z"
        campaignManager.usNatConsentData = usnatMock.copy(expirationDate = "2024-10-27T17:15:57.006Z")

        campaignManager.isGdprExpired.assertFalse()
        campaignManager.isCcpaExpired.assertTrue()
        campaignManager.isUsnatExpired.assertFalse()
    }

    @Test
    fun given_an_expired_USNAT_isUsnatExpired_RETURN_true() {

        dataStorage.gdprExpirationDate = "2024-10-27T17:15:57.006Z"
        dataStorage.ccpaExpirationDate = "2024-10-27T17:15:57.006Z"
        campaignManager.usNatConsentData = usnatMock.copy(expirationDate = "2022-10-27T17:15:57.006Z")

        campaignManager.isGdprExpired.assertFalse()
        campaignManager.isCcpaExpired.assertFalse()
        campaignManager.isUsnatExpired.assertTrue()
    }

    @Test
    fun given_gdpr_vendor_list_id_changed_from_null_to_something_THEN_should_return_false() {
        // GIVEN
        val gdprVendorListId = "new_gdpr_vendor_list_id"

        // WHEN
        val actual = campaignManager.hasGdprVendorListIdChanged(gdprVendorListId)

        // THEN
        actual.assertFalse()
    }

    @Test
    fun given_gdpr_vendor_list_id_did_not_change_THEN_should_return_false() {
        campaignManager.metaDataResp = MetaDataResponse(
            gdpr = MetaDataResponse.MetaDataResponseGDPR(
                vendorListId = "123",
                additionsChangeDate = "",
                applies = false,
                childPmId = null,
                legalBasisChangeDate = "",
                sampleRate = 1.0f
            ),
            usnat = null,
            ccpa = null
        )
        campaignManager.hasGdprVendorListIdChanged("123").assertFalse()
    }

    @Test
    fun given_gdpr_vendor_list_id_changed_from_something_to_something_else_THEN_should_return_true() {
        campaignManager.metaDataResp = MetaDataResponse(
            gdpr = MetaDataResponse.MetaDataResponseGDPR(
                vendorListId = "123",
                additionsChangeDate = "",
                applies = false,
                childPmId = null,
                legalBasisChangeDate = "",
                sampleRate = 1.0f
            ),
            usnat = null,
            ccpa = null
        )
        campaignManager.hasGdprVendorListIdChanged("321").assertTrue()
    }

    @Test
    fun given_gdpr_vendor_list_id_changed_from_something_to_null_THEN_should_return_false() {
        campaignManager.metaDataResp = MetaDataResponse(
            gdpr = MetaDataResponse.MetaDataResponseGDPR(
                vendorListId = "123",
                additionsChangeDate = "",
                applies = false,
                childPmId = null,
                legalBasisChangeDate = "",
                sampleRate = 1.0f
            ),
            usnat = null,
            ccpa = null
        )
        campaignManager.hasGdprVendorListIdChanged(null).assertFalse()
    }

    @Test
    fun given_usnat_vendor_list_id_changed_from_null_to_something_THEN_should_return_false() {
        campaignManager
            .hasUsNatVendorListIdChanged("new_usnat_vendor_list_id")
            .assertFalse()
    }

    @Test
    fun given_usnat_vendor_list_id_did_not_change_THEN_should_return_false() {
        campaignManager.metaDataResp = MetaDataResponse(
            gdpr = null,
            usnat = MetaDataResponse.MetaDataResponseUSNat(
                applies = false,
                sampleRate = 1.0f,
                additionsChangeDate = "",
                applicableSections = emptyList(),
                vendorListId = "123"
            ),
            ccpa = null
        )
        campaignManager
            .hasUsNatVendorListIdChanged("123")
            .assertFalse()
    }

    @Test
    fun given_usnat_vendor_list_id_changed_from_something_to_something_else_THEN_should_return_true() {
        campaignManager.metaDataResp = MetaDataResponse(
            gdpr = null,
            usnat = MetaDataResponse.MetaDataResponseUSNat(
                applies = false,
                sampleRate = 1.0f,
                additionsChangeDate = "",
                applicableSections = emptyList(),
                vendorListId = "123"
            ),
            ccpa = null
        )
        campaignManager
            .hasUsNatVendorListIdChanged("abc")
            .assertTrue()
    }

    @Test
    fun given_usnat_vendor_list_id_changed_from_something_to_null_THEN_should_return_false() {
        campaignManager.handleMetaDataResponse(
            MetaDataResponse(
                gdpr = null,
                usnat = MetaDataResponse.MetaDataResponseUSNat(
                    applies = false,
                    sampleRate = 1.0f,
                    additionsChangeDate = "",
                    applicableSections = emptyList(),
                    vendorListId = "123"
                ),
                ccpa = null
            )
        )
        campaignManager
            .hasUsNatVendorListIdChanged(null)
            .assertFalse()
    }

    @Test
    fun resets_usnatSampled_when_usnatSampleRate_changes() {
        dataStorage.usnatSampleRate = 1.0
        dataStorage.usnatSampled = true
        val newRate = 0.5
        campaignManager.handleMetaDataResponse(
            MetaDataResponse(
                gdpr = null,
                usnat = MetaDataResponse.MetaDataResponseUSNat(
                    applies = false,
                    sampleRate = newRate.toFloat(),
                    additionsChangeDate = "",
                    applicableSections = emptyList(),
                    vendorListId = ""
                ),
                ccpa = null
            )
        )
        dataStorage.usnatSampleRate.assertEquals(newRate)
        dataStorage.usnatSampled.assertNull()
    }

    @Test
    fun calls_pvData_if_sampled_true() {
        dataStorage.usnatSampleRate = 1.0
        dataStorage.usnatSampled = true
        val newRate = 0.5
        campaignManager.handleMetaDataResponse(
            MetaDataResponse(
                gdpr = null,
                usnat = MetaDataResponse.MetaDataResponseUSNat(
                    applies = false,
                    sampleRate = newRate.toFloat(),
                    additionsChangeDate = "",
                    applicableSections = emptyList(),
                    vendorListId = ""
                ),
                ccpa = null
            )
        )
        dataStorage.usnatSampleRate.assertEquals(newRate)
        dataStorage.usnatSampled.assertNull()
    }
}
