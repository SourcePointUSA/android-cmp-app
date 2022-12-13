package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertNotNull
import com.example.uitestutil.assertTrue
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.IABTCF_KEY_PREFIX
import org.junit.Test
import org.junit.runner.RunWith
import java.util.* //ktlint-disable

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageGdprImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun check_DataStorage_TcData_gets_stored() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorageGdpr.create(appContext).apply { clearAll() }
        val map = TreeMap<String, String>()
        (1..10).forEach { map["IABTCF_$it"] = "$it" }
        storage.tcData = map
        val output = storage.tcData
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
        storage.gdprConsentUuid = "uuid"
        storage.saveEuConsent("eu")
        storage.saveMetaData("meta")
        storage.gdprApplies = true
        storage.saveGdpr("{\"type\":\"GDPR\"}")
        storage.tcData = getMap()

        storage.getAuthId().assertEquals("auth")
        storage.gdprConsentUuid.assertEquals("uuid")
        storage.getEuConsent().assertEquals("eu")
        storage.getMetaData().assertEquals("meta")
        storage.gdprApplies.assertTrue()
        storage.getGdpr().assertEquals("{\"type\":\"GDPR\"}")
        storage.tcData.assertEquals(getMap())

        storage.clearInternalData()

        /** clearInternalData delete only these prefs */
        storage.getAuthId().assertEquals("")
        storage.gdprConsentUuid.assertEquals(null)
        storage.getEuConsent().assertNotNull()
        storage.getMetaData().assertEquals("")

        /** clearInternalData DOES NOT delete these prefs */
        storage.gdprApplies.assertTrue()
        storage.tcData.assertEquals(getMap())
        storage.getGdpr().assertEquals("{\"type\":\"GDPR\"}")
    }

    private fun getMap(): TreeMap<String, String> {
        return TreeMap<String, String>().apply { this["${IABTCF_KEY_PREFIX}key"] = "value" }
    }
}
