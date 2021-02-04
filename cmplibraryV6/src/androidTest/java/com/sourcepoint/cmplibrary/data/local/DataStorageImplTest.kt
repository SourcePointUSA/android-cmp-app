package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.IABTCF_KEY_PREFIX
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageImplTest {

    @Test
    fun check_DataStorage_TcData_gets_stored() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorage.create(appContext).apply { clearAll() }
        val map = DeferredMap(true)
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
        val storage = DataStorage.create(appContext).apply { clearAll() }

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

    private fun getMap(): DeferredMap {
        return DeferredMap(false).apply { this["${IABTCF_KEY_PREFIX}key"] = "value" }
    }
}
