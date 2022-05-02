package com.sourcepoint.cmplibrary.campaign

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertNotNull
import com.example.uitestutil.assertNull
import com.example.uitestutil.jsonFile2String
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
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
        groupPmId = "111"
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
        DataStorage.create(appContext, gdprDs, ccpaDs)
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
        3000
    )

    private val cm by lazy {
        CampaignManager.create(ds, spConfig, MessageLanguage.ENGLISH)
    }

    @Before
    fun setup() {
        ds.clearAll()
    }

    @Test
    fun `GIVEN_a_groupPmId_RETURN_the_pmId`() {

        val sut = CampaignManager.create(ds, spConfig, MessageLanguage.ENGLISH).apply {
            addCampaign(CampaignType.GDPR, gdpr)
            addCampaign(CampaignType.CCPA, ccpa)
        }

        sut
            .getPmConfig(CampaignType.GDPR, "11", PMTab.PURPOSES, false)
            .getOrNull()!!
            .messageId
            .assertEquals("11")

        sut
            .getPmConfig(CampaignType.CCPA, "22", PMTab.PURPOSES, false)
            .getOrNull()!!
            .messageId
            .assertEquals("22")

        // set the groupPmId

        ds.gdprChildPmId = "33"
        ds.ccpaChildPmId = "44"

        sut
            .getPmConfig(CampaignType.GDPR, "11", PMTab.PURPOSES, true)
            .getOrNull()!!
            .messageId
            .assertEquals("33")
        sut
            .getPmConfig(CampaignType.CCPA, "22", PMTab.PURPOSES, true)
            .getOrNull()!!
            .messageId
            .assertEquals("44")

        sut
            .getPmConfig(CampaignType.GDPR, "11", PMTab.PURPOSES, false)
            .getOrNull()!!
            .messageId
            .assertEquals("11")
        sut
            .getPmConfig(CampaignType.CCPA, "22", PMTab.PURPOSES, false)
            .getOrNull()!!
            .messageId
            .assertEquals("44") // it is 44 because the feature is not yet implemented on ccpa
    }

    @Test
    fun `GIVEN_a_ds_with_a_resp_saved_RETURN_a_MessageRequest_with_meta_and_uuid_null`() {
        // create a message response DTO
        val unifiedMess =
            JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()).toUnifiedMessageRespDto()

        // store the message response dto as we already did the first call getMessage
        // this means that we already have in memory the GDPR and CCPA campaign
        val ccpaLocal = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        val gdprLocal = unifiedMess.campaigns.find { it is Gdpr } as Gdpr
        cm.run {
            saveCcpa(ccpaLocal)
            saveGdpr(gdprLocal)
        }

        // execute the test
        val sut = CampaignManager.create(ds, spConfig, MessageLanguage.ENGLISH).apply {
            addCampaign(CampaignType.GDPR, gdpr)
            addCampaign(CampaignType.CCPA, ccpa)
        }
    }

    @Test
    fun `GIVEN_an_Gdpr_object_STORE_it_into_the_local_data_storage`() {

        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr

        val dataStorageGdpr = DataStorageGdpr.create(appContext)
        val dataStorageCcpa = DataStorageCcpa.create(appContext)
        val dataStorage = DataStorage.create(appContext, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val sut = CampaignManager.create(dataStorage, spConfig, MessageLanguage.ENGLISH)

        sut.saveGdpr(gdpr)
        val res = sut.getGdpr()

        val t: Gdpr = (res as Either.Right<Gdpr>).getOrNull().assertNotNull()!!

        t.run {
            userConsent.let {
                it.uuid.assertNull()
                it.childPmId.assertNull()
                it.grants.size.assertEquals(633)
                it.euconsent.assertEquals("CPEpDOrPEpDOrHIABCENBVCgAAAAAH_AAAYgAAAOQAIMBAUAAAEEAAAIECIQAAQAiQAAAABBCABQJIAEqgACVwERAIAACAxAQgAAgBAQgwCAAAAAJIAgBACwQCAAiAQAAgAEAAAAEIAIDACQEAAAEAJCAAiACECAgiAAg5CAgIACCAFABAAAuJDACAMooASBBgHBIB4ACAArACOAGWANQAgABSwDFAGsAN4AfIBMQCZQFoAMCDQDQArADLAGoAOwAgABBQClgGsAN4AdUA-QC6AGBBwAIDfxEAsAKwAywBqADsAIAAUsA1gB1QD5ALoAYEJAAgN_FQCQBHADLAGoAOwApYBvAExAMCAbkMgEACOAGWANQAdgBSwDeAJiAYEOgFABWAEcANQAsQBdQDFAHUAWgAu0Bog8ACA38hAHAEcANQAoYBigDqALtAaISgEAAIADaAI4AuoBigDqAHyAXaTAAgN_KQAwChgK2AXaVAAgN_AA.YAAAAAAAAAAA")
            }
        }
    }

    @Test
    fun `GIVEN_an_Ccpa_object_STORE_it_into_the_local_data_storage`() {

        val unifiedMess =
            JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()).toUnifiedMessageRespDto()

        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr

        val dataStorageGdpr = DataStorageGdpr.create(appContext)
        val dataStorageCcpa = DataStorageCcpa.create(appContext)
        val dataStorage = DataStorage.create(appContext, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }
        val sut = CampaignManager.create(dataStorage, spConfig, MessageLanguage.ENGLISH)

        sut.saveCcpa(ccpa)
        val res = sut.getCcpa()

        (res as Either.Right).r.run {
            userConsent.let { uc ->
                uc.rejectedCategories.sortedBy { it.hashCode() }
                    .assertEquals(ccpa.userConsent.rejectedCategories.sortedBy { it.hashCode() })
                uc.rejectedVendors.sortedBy { it.hashCode() }
                    .assertEquals(ccpa.userConsent.rejectedVendors.sortedBy { it.hashCode() })
                uc.status.assertEquals(ccpa.userConsent.status)
                uc.uspstring.assertEquals(ccpa.userConsent.uspstring)
            }
        }
    }
}
