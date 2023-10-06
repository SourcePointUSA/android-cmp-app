package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageGdprImplTest {

    @Test
    fun clear_data_DataStorage() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorageGdpr.create(appContext).apply { clearAll() }

        storage.saveAuthId("auth")
        storage.gdprConsentUuid = "uuid"
        storage.gdprApplies = true
        storage.saveGdpr("{\"type\":\"GDPR\"}")

        storage.gdprConsentUuid.assertEquals("uuid")
        storage.gdprApplies.assertTrue()
        storage.getGdpr().assertEquals("{\"type\":\"GDPR\"}")

        storage.clearInternalData()

        /** clearInternalData delete only these prefs */
        storage.gdprConsentUuid.assertEquals(null)

        /** clearInternalData DOES NOT delete these prefs */
        storage.gdprApplies.assertTrue()
        storage.getGdpr().assertEquals("{\"type\":\"GDPR\"}")
    }
}
