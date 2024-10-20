package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertFalse
import com.example.uitestutil.assertNull
import com.example.uitestutil.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }
    private val gdprStorage by lazy { DataStorageGdpr.create(appContext) }
    private val ccpaStorage by lazy { DataStorageCcpa.create(appContext) }
    private val usNatStorage by lazy { DataStorageUSNat.create(appContext) }
    private val dataStorage by lazy { DataStorage.create(appContext, gdprStorage, ccpaStorage, usNatStorage) }

    @Before
    fun setup() {
        dataStorage.clearAll()
    }

    @Test
    fun check_used_key_for_save_get_LocalState() {
        dataStorage.run {
            saveLocalState("test_ls")
            getLocalState().assertEquals("test_ls")
        }
    }

    @Test
    fun test_getBoolean() {
        dataStorage.preference.getBoolean("foo").assertNull()
        dataStorage.preference.edit().putBoolean("foo", true).apply()
        dataStorage.preference.getBoolean("foo")!!.assertTrue()
    }

    @Test
    fun test_putBoolean() {
        dataStorage.preference.putBoolean("foo", false)
        dataStorage.preference.getBoolean("foo", true).assertFalse()

        dataStorage.preference.putBoolean("foo", null)
        dataStorage.preference.getBoolean("foo").assertNull()
    }

    @Test
    fun test_putFloat() {
        dataStorage.preference.putFloat("foo", 1.0f)
        dataStorage.preference.getFloat("foo", 0.0f).assertEquals(1.0f)

        dataStorage.preference.putFloat("foo", null)
        dataStorage.preference.contains("foo").assertFalse()
    }

    @Test
    fun test_putString() {
        dataStorage.preference.putString("foo", "bar")
        dataStorage.preference.getString("foo", "").assertEquals("bar")

        dataStorage.preference.putFloat("foo", null)
        dataStorage.preference.contains("foo").assertFalse()
    }
}
