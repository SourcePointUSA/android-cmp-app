package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageImplTest {

    @Test
    fun check_DataStorage_TcData_gets_stored() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorage.create(appContext)
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
        val storage = DataStorage.create(appContext)

        storage.saveAuthId("auth")
        storage.saveConsentUuid("uuid")
        storage.saveEuConsent("eu")
        storage.saveMetaData("meta")

        storage.getAuthId().assertEquals("auth")
        storage.getConsentUuid().assertEquals("uuid")
        storage.getEuConsent().assertEquals("eu")
        storage.getMetaData().assertEquals("meta")

        storage.clearInternalData()

        storage.getAuthId().assertEquals("")
        storage.getConsentUuid().assertEquals("")
        storage.getEuConsent().assertEquals("")
        storage.getMetaData().assertEquals("")
    }
}
