package com.sourcepoint.cmplibrary

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.* //ktlint-disable
import com.example.uitestutil.assertNotNull
import com.example.uitestutil.assertNull
import com.example.uitestutil.assertTrue
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepoint.cmplibrary.util.ccpaApplies
import com.sourcepoint.cmplibrary.util.gdprApplies
import com.sourcepoint.cmplibrary.util.userConsents
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SpUtilsTest {

    private val appCtx by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

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

    @Test
    fun `CALLING_userConsents_return_only_gdpr`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)

        userConsents(appCtx, spConfig).run {
            ccpa.assertNull()
            gdpr.assertNull()
        }

        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr

        campaignManager.saveGdpr(gdpr)

        userConsents(appCtx, spConfig).run {
            ccpa.assertNull()
            gdpr.assertNotNull()
        }
    }

    @Test
    fun `CALLING_userConsents_return_only_ccpa`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)

        userConsents(appCtx, spConfig).run {
            ccpa.assertNull()
            gdpr.assertNull()
        }

        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa

        campaignManager.saveCcpa(ccpa)

        userConsents(appCtx, spConfig).run {
            ccpa.assertNotNull()
            gdpr.assertNull()
        }
    }

    @Test
    fun `CALLING_userConsents_return_gdpr_and_gdpr`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)

        userConsents(appCtx, spConfig).run {
            ccpa.assertNull()
            gdpr.assertNull()
        }

        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()).toUnifiedMessageRespDto()

        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr
        campaignManager.saveGdpr(gdpr)
        campaignManager.saveCcpa(ccpa)

        userConsents(appCtx, spConfig).run {
            ccpa.assertNotNull()
            gdpr.assertNotNull()
        }
    }

    @Test
    fun `CALLING_gdpr-ccpa-Applies_RETURN_true`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)
        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa_apply_true.json".jsonFile2String()).toUnifiedMessageRespDto()
        val gdpr = (unifiedMess.campaigns.find { it is Gdpr } as Gdpr)
        val ccpa = (unifiedMess.campaigns.find { it is Ccpa } as Ccpa)

        campaignManager.saveGdpr(gdpr)
        campaignManager.saveCcpa(ccpa)

        gdprApplies(appCtx).assertTrue()
        ccpaApplies(appCtx).assertTrue()
    }

    @Test
    fun `CALLING_gdpr-ccpa-Applies_RETURN_false`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)
        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa_apply_false.json".jsonFile2String()).toUnifiedMessageRespDto()
        val gdpr = (unifiedMess.campaigns.find { it is Gdpr } as Gdpr)
        val ccpa = (unifiedMess.campaigns.find { it is Ccpa } as Ccpa)

        campaignManager.saveGdpr(gdpr)
        campaignManager.saveCcpa(ccpa)

        gdprApplies(appCtx).assertFalse()
        ccpaApplies(appCtx).assertFalse()
    }

    @Test
    fun `CALLING_ccpaApplies_RETURN_true_gdpr_false`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)
        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa_apply_true.json".jsonFile2String()).toUnifiedMessageRespDto()
        val ccpa = (unifiedMess.campaigns.find { it is Ccpa } as Ccpa)

        campaignManager.saveCcpa(ccpa)

        gdprApplies(appCtx).assertFalse()
        ccpaApplies(appCtx).assertTrue()
    }

    @Test
    fun `CALLING_gdprApplies_RETURN_true_ccpa_false`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val campaignManager: CampaignManager = CampaignManager.create(dataStorage, spConfig)
        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa_apply_true.json".jsonFile2String()).toUnifiedMessageRespDto()
        val gdpr = (unifiedMess.campaigns.find { it is Gdpr } as Gdpr)
        val ccpa = (unifiedMess.campaigns.find { it is Ccpa } as Ccpa)

        campaignManager.saveGdpr(gdpr)

        gdprApplies(appCtx).assertTrue()
        ccpaApplies(appCtx).assertFalse()
    }

    @Test
    fun `SAVE_ccpa_andgdpr_groupId`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }

        dataStorage.gdprChildPmId = "1"
        dataStorage.ccpaChildPmId = "2"

        dataStorage.gdprChildPmId.assertEquals("1")
        dataStorage.ccpaChildPmId.assertEquals("2")
    }
}
