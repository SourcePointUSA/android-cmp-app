package com.sourcepoint.cmplibrary

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertNotNull
import com.example.uitestutil.assertNull
import com.example.uitestutil.jsonFile2String
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.util.userConsents
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SpUtilsTest {
    private val appCtx by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun `CALLING_userConsents_return_only_gdpr`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }

        userConsents(appCtx).run {
            ccpa.assertNull()
            gdpr.assertNull()
        }

        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr

        dataStorage.saveGdpr(gdpr)

        userConsents(appCtx).run {
            ccpa.assertNull()
            gdpr.assertNotNull()
        }
    }

    @Test
    fun `CALLING_userConsents_return_only_ccpa`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }

        userConsents(appCtx).run {
            ccpa.assertNull()
            gdpr.assertNull()
        }

        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa

        dataStorage.saveCcpa(ccpa)

        userConsents(appCtx).run {
            ccpa.assertNotNull()
            gdpr.assertNull()
        }
    }

    @Test
    fun `CALLING_userConsents_return_gdpr_and_gdpr`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }

        userConsents(appCtx).run {
            ccpa.assertNull()
            gdpr.assertNull()
        }

        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()).toUnifiedMessageRespDto()

        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr
        dataStorage.saveGdpr(gdpr)
        dataStorage.saveCcpa(ccpa)

        userConsents(appCtx).run {
            ccpa.assertNotNull()
            gdpr.assertNotNull()
        }
    }
}
