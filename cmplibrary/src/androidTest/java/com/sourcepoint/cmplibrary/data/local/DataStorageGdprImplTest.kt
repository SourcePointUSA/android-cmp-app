package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
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
        storage.saveGdpr("{\"type\":\"GDPR\"}")

        storage.getGdpr().assertEquals("{\"type\":\"GDPR\"}")

        storage.clearInternalData()

        /** clearInternalData DOES NOT delete these prefs */
        storage.getGdpr().assertEquals("{\"type\":\"GDPR\"}")
    }
}
