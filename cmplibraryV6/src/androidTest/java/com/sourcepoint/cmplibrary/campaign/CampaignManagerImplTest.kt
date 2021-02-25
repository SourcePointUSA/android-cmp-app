package com.sourcepoint.cmplibrary.campaign

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertNull
import com.example.uitestutil.jsonFile2String
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CCPACampaign
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import org.json.JSONObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class CampaignManagerImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    private val gdpr = GDPRCampaign(
        accountId = 22,
        propertyId = 10589,
        propertyName = "https://unified.mobile.demo",
        pmId = "404472"
    )

    private val ccpa = CCPACampaign(
        accountId = 22,
        propertyId = 10589,
        propertyName = "https://unified.mobile.demo",
        pmId = "404472"
    )

    private val ds by lazy {
        val gdprDs = DataStorageGdpr.create(appContext)
        val ccpaDs = DataStorageCcpa.create(appContext)
        DataStorage.create(appContext, gdprDs, ccpaDs)
    }

    @Before
    fun setup() {
        ds.clearAll()
    }

    @Test
    fun `GIVEN_an_empty_ds_RETURN_a_MessageRequest_with_meta_and_uuid_null`() {

        val sut = CampaignManager.create(ds).apply {
            addCampaign(Legislation.GDPR, gdpr)
            addCampaign(Legislation.CCPA, ccpa)
        }

        val output = sut.getMessageReq()

        output.campaigns.ccpa!!.run {
            meta.assertNull()
            uuid.assertNull()
        }
        output.campaigns.gdpr!!.run {
            meta.assertNull()
            uuid.assertNull()
        }
    }

    @Test
    fun `GIVEN_a_ds_with_a_resp_saved_RETURN_a_MessageRequest_with_meta_and_uuid_null`() {
        // create a message response DTO
        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()).toUnifiedMessageRespDto()

        // store the message response dto as we already did the first call getMessage
        // this means that we already have in memory the GDPR and CCPA campaign
        ds.run {
            saveCcpa(unifiedMess.campaigns.find { it is Ccpa } as Ccpa)
            saveGdpr(unifiedMess.campaigns.find { it is Gdpr } as Gdpr)
        }

        // execute the test
        val sut = CampaignManager.create(ds).apply {
            addCampaign(Legislation.GDPR, gdpr)
            addCampaign(Legislation.CCPA, ccpa)
        }

        val output = sut.getMessageReq()

        output.campaigns.ccpa!!.run {
            meta.assertEquals("{\"mmsCookies\":[\"_sp_v1_uid=1:608:84b4e9d7-4dde-4eda-82ab-284a9aeb9fed\",\"_sp_v1_csv=1\",\"_sp_v1_lt=1:\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbLKK83J0YlRSkVil4AlqmtrlXTgyqKRGXkghkFtLC59OCWUYgEO1mB4eQAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_data=2:289044:1613636576:0:1:0:1:0:0:885b0124-dbce-4102-b017-e602962a39ed:-1\"],\"messageId\":425813,\"dnsDisplayed\":true}")
            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
        }
        output.campaigns.gdpr!!.run {
            meta.assertEquals("{\"mmsCookies\":[\"_sp_v1_uid=1:570:b0810b6d-9dac-4458-9227-7d656973759c\",\"_sp_v1_csv=1\",\"_sp_v1_lt=1:\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKRmbkgRgGtbE6MUqpIGZeaU4OkF0CVlBdi1tCKRYAmuD4I1IAAAA%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_data=2:289044:1613636575:0:1:0:1:0:0:885b0124-dbce-4102-b017-e602962a39ed:-1\"],\"messageId\":425812}")
            uuid.assertEquals("a42f93fc-282c-422d-89f2-841e04d9217f")
        }
    }
}
