package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.HARDCODED_LOCAL_DATA_VERSION
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }
    private val gdprStorage by lazy { DataStorageGdpr.create(appContext) }
    private val ccpaStorage by lazy { DataStorageCcpa.create(appContext) }
    private val sut by lazy { DataStorage.create(appContext, gdprStorage, ccpaStorage) }

    @Before
    fun setup() {
        sut.clearAll()
    }

    @Test
    fun check_used_key_for_save_get_LocalState() {
        sut.run {
            saveLocalState("test_ls")
            getLocalState().assertEquals("test_ls")
        }
    }

    @Test
    fun localDataVersion_when_value_is_set_then_should_return_the_same_value() {
        val fakeLocalDataVersion = 5
        sut.localDataVersion = fakeLocalDataVersion
        sut.localDataVersion.assertEquals(fakeLocalDataVersion)
    }

    @Test
    fun updateLocalDataVersion_when_called_then_should_set_local_version_to_the_hardcoded_one() {
        sut.updateLocalDataVersion()
        sut.localDataVersion.assertEquals(HARDCODED_LOCAL_DATA_VERSION)
    }
}
