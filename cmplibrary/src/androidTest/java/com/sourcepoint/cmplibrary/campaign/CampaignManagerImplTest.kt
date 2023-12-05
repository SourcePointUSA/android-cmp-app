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
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatusResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.MetaDataResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentData
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
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

    private val ds by lazy {
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
        propertyId = 1234,
        messageTimeout = 3000,
    )

    private val cm by lazy {
        CampaignManager.create(ds, spConfig)
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
            webConsentPayload = null
        )
    }

    @Before
    fun setup() {
        ds.clearAll()
    }

    @Test
    fun `GIVEN_a_groupPmId_RETURN_the_pmId`() {

        val sut = CampaignManager.create(ds, spConfig).apply {
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

        ds.gdprChildPmId = "33"
        ds.ccpaChildPmId = "44"

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
    fun `GIVEN_a_v7_consent_status_STORE_it_into_the_local_data_storage`() {
        val json = "v7/consent_status_with_auth_id.json".file2String()
        val obj = JsonConverter.converter.decodeFromString<ConsentStatusResp>(json)

        cm.gdprConsentStatus = obj.consentStatusData!!.gdpr
        cm.ccpaConsentStatus = obj.consentStatusData!!.ccpa
        cm.messagesOptimizedLocalState = obj.localState

        cm.gdprConsentStatus!!.also {
            it.uuid.assertEquals("69b29ebc-c358-4d7f-9220-38ca2f00125b_1_2_3_4_5_6_7_8_9_10")
            it.dateCreated.toString().assertEquals("2022-08-25T20:56:38.551Z")
            it.TCData!!.size.assertEquals(27)
        }
        cm.ccpaConsentStatus!!.also {
            it.uuid.assertEquals("e47e539d-41dd-442b-bb08-5cf52b1e33d4")
            it.dateCreated.toString().assertEquals("2022-08-25T20:56:39.010Z")
        }

        cm.gdprConsentStatus = null
        cm.ccpaConsentStatus = null

        cm.gdprConsentStatus.assertNull()
        cm.ccpaConsentStatus.assertNull()
        cm.messagesOptimizedLocalState.assertNotNull()
    }

    @Test
    fun `GIVEN_a_v7_MetaData_STORE_it_into_the_local_data_storage`() {
        val json = "v7/meta_data.json".file2String()
        val obj = JsonConverter.converter.decodeFromString<MetaDataResp>(json)

        cm.metaDataResp = obj

        cm.metaDataResp?.run {
            gdpr?.also {
                it.applies!!.assertFalse()
                it.id!!.assertEquals("5fa9a8fda228635eaf24ceb5")
            }
            ccpa?.also {
                it.applies!!.assertTrue()
            }
        }

        cm.metaDataResp = null

        cm.metaDataResp.assertNull()
    }

    @Test
    fun `GIVEN_an_expired_GDPR_and_a_valid_CCPA_campaign_DELETE_only_the_GDPR_consent`() {
        val json = JSONObject("v7/expired_gdpr_valid_ccpa.json".file2String())
        appContext.storeTestDataObj(json.toList())
        ds.deleteGdprConsent()
        appContext.spEntries().toList().find { it.first.contains("GDPR") }.assertNull()
    }

    @Test
    fun `GIVEN_an_expired_CCPA_and_a_valid_GDPR_campaign_DELETE_only_the_GDPR_consent`() {
        val json = JSONObject("v7/expired_ccpa_valid_gdpr.json".file2String())
        appContext.storeTestDataObj(json.toList())
        ds.deleteCcpaConsent()
        appContext.spEntries().toList().find { it.first.contains("CCPA") }.assertNull()
    }

    @Test
    fun `GIVEN_an_expired_GDPR_isGdprExpired_RETURN_true`() {

        ds.gdprExpirationDate = "2022-10-27T17:15:57.006Z"
        ds.ccpaExpirationDate = "2024-10-27T17:15:57.006Z"
        cm.usNatConsentData = usnatMock.copy(expirationDate = "2024-10-27T17:15:57.006Z")

        cm.isGdprExpired.assertTrue()
        cm.isCcpaExpired.assertFalse()
        cm.isUsnatExpired.assertFalse()
    }

    @Test
    fun `GIVEN_an_expired_CCPA_isCcpaExpired_RETURN_true`() {

        ds.gdprExpirationDate = "2024-10-27T17:15:57.006Z"
        ds.ccpaExpirationDate = "2022-10-27T17:15:57.006Z"
        cm.usNatConsentData = usnatMock.copy(expirationDate = "2024-10-27T17:15:57.006Z")

        cm.isGdprExpired.assertFalse()
        cm.isCcpaExpired.assertTrue()
        cm.isUsnatExpired.assertFalse()
    }

    @Test
    fun `GIVEN_an_expired_USNAT_isUsnatExpired_RETURN_true`() {

        ds.gdprExpirationDate = "2024-10-27T17:15:57.006Z"
        ds.ccpaExpirationDate = "2024-10-27T17:15:57.006Z"
        cm.usNatConsentData = usnatMock.copy(expirationDate = "2022-10-27T17:15:57.006Z")

        cm.isGdprExpired.assertFalse()
        cm.isCcpaExpired.assertFalse()
        cm.isUsnatExpired.assertTrue()
    }
}
