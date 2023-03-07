package com.sourcepoint.cmplibrary

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.* //ktlint-disable
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SpUtilsTest {

    private val appCtx by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun `SAVE_ccpa_andgdpr_groupId`() {
        val dataStorageGdpr = DataStorageGdpr.create(appCtx)
        val dataStorageCcpa = DataStorageCcpa.create(appCtx)
        val dataStorage = DataStorage.create(appCtx, dataStorageGdpr, dataStorageCcpa).apply { clearAll() }

        dataStorage.gdprChildPmId = "1"
        dataStorage.ccpaChildPmId = "2"

        dataStorage.gdprChildPmId.assertEquals("1")
        dataStorage.ccpaChildPmId.assertEquals("2")
    }
}
