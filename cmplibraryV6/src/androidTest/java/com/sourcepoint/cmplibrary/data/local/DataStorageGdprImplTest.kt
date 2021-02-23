package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.jsonFile2String
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.IABTCF_KEY_PREFIX
import com.sourcepoint.cmplibrary.data.network.converter.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.util.Either
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import java.util.* //ktlint-disable

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageGdprImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun `GIVEN_an_Gdpr_object_STORE_it_into_the_local_data_storage`() {

        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr

        val sut = DataStorageGdpr.create(appContext)
        sut.saveGdpr(gdpr)
        val res = sut.getGdpr()

        (res as Either.Right).r.run {
            uuid.assertEquals(gdpr.uuid)
            meta.assertEquals(gdpr.meta)
            gdprApplies.assertEquals(gdpr.gdprApplies)
            userConsent!!.let { uc ->
                uc.acceptedCategories.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.acceptedCategories.sortedBy { it.hashCode() })
                uc.acceptedVendors.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.acceptedVendors.sortedBy { it.hashCode() })
                uc.legIntCategories.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.legIntCategories.sortedBy { it.hashCode() })
                uc.specialFeatures.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.specialFeatures.sortedBy { it.hashCode() })
            }
        }
    }

    @Test
    fun `GIVEN_an_Ccpa_object_STORE_it_into_the_local_data_storage`() {

        val unifiedMess = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()).toUnifiedMessageRespDto()

        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr

        val sut = DataStorageCcpa.create(appContext)
        sut.saveCcpa(ccpa)
        val res = sut.getCcpa()

        (res as Either.Right).r.run {
            uuid.assertEquals(ccpa.uuid)
            meta.assertEquals(ccpa.meta)
            ccpaApplies.assertEquals(ccpa.ccpaApplies)
            userConsent.let { uc ->
                uc.rejectedCategories.sortedBy { it.hashCode() }.assertEquals(ccpa.userConsent!!.rejectedCategories.sortedBy { it.hashCode() })
                uc.rejectedVendors.sortedBy { it.hashCode() }.assertEquals(ccpa.userConsent!!.rejectedVendors.sortedBy { it.hashCode() })
                uc.status.assertEquals(ccpa.userConsent.status)
                uc.uspstring.assertEquals(ccpa.userConsent.uspstring)
            }
        }
    }

    @Test
    fun check_DataStorage_TcData_gets_stored() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorageGdpr.create(appContext).apply { clearAll() }
        val map = TreeMap<String, String>()
        (1..10).forEach { map["IABTCF_$it"] = "$it" }
        storage.saveTcData(map)
        val output = storage.getTcData()
        map.forEach {
            output[it.key].assertEquals(it.value)
        }
    }

    @Test
    fun clear_data_DataStorage() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorageGdpr.create(appContext).apply { clearAll() }

        storage.saveAuthId("auth")
        storage.saveConsentUuid("uuid")
        storage.saveEuConsent("eu")
        storage.saveMetaData("meta")
        storage.saveAppliedLegislation("GDPR")
        storage.saveTcData(getMap())

        storage.getAuthId().assertEquals("auth")
        storage.getConsentUuid().assertEquals("uuid")
        storage.getEuConsent().assertEquals("eu")
        storage.getMetaData().assertEquals("meta")
        storage.getAppliedLegislation().assertEquals("GDPR")
        storage.getTcData().assertEquals(getMap())

        storage.clearInternalData()

        /** clearInternalData delete only these prefs */
        storage.getAuthId().assertEquals("")
        storage.getConsentUuid().assertEquals("")
        storage.getEuConsent().assertEquals("")
        storage.getMetaData().assertEquals("")

        /** clearInternalData DOES NOT delete these prefs */
        storage.getAppliedLegislation().assertEquals("GDPR")
        storage.getTcData().assertEquals(getMap())
    }

    private fun getMap(): TreeMap<String, String> {
        return TreeMap<String, String>().apply { this["${IABTCF_KEY_PREFIX}key"] = "value" }
    }
}
