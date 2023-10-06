package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.* // ktlint-disable

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageCcpaImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun check_DataStorage_TcData_gets_stored() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorageCcpa.create(appContext).apply { clearAll() }
        val map = TreeMap<String, String>()
    }

    @Test
    fun clear_data_DataStorage() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val storage = DataStorageCcpa.create(appContext).apply { clearAll() }

        storage.saveCcpa("{\"type\":\"Ccpa\"}")

        storage.getCcpa().assertEquals("{\"type\":\"Ccpa\"}")

        storage.clearAll()

        storage.getCcpa().assertNull()
    }
}
