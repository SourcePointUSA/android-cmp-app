package com.sourcepointmeta.metaapp

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.assertFalse
import com.example.uitestutil.assertTrue
import com.sourcepointmeta.metaapp.TestUseCaseMeta.Companion.checkMessageDisplayed
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.data.localdatasource.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    lateinit var scenario: ActivityScenario<MainActivity>

    @After
    fun cleanup() {
        if (this::scenario.isLateinit) scenario.close()
    }

    @Test
    fun test1() = runBlocking<Unit> {

        scenario = launchActivity()

        checkMessageDisplayed()

    }

    @Test
    fun db_test() = runBlocking<Unit> {

        scenario = launchActivity()

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val db = createDb(appContext)

        val ds = LocalDataSource.create(db)

        ds.deleteAll()

        (ds.fetchTargetingParams("test") as? Either.Right)?.r!!.size.assertEquals(0)


        val tp = listOf(
            TargetingParam("test", "key1", "val1"),
            TargetingParam("test", "key2", "val2"),
            TargetingParam("test", "key3", "val3"),
        )

        val property = Property(
            account_id = 1,
            property_name = "test",
            property_id = 2,
            pm_id = "",
            auth_Id = null,
            message_language = null,
            pm_tab = null,
            is_staging = false,
            targetingParameters = tp
        )

        ds.storeProperty(property)
        val prop = (ds.fetchProperties() as Either.Right).r.first()
        ds.storeProperty(prop.copy(is_staging = true, targetingParameters = prop.targetingParameters.map { it.copy(value = "porcodio") }))
        (ds.fetchProperties() as Either.Right).r.first().is_staging.assertTrue()
        ds.storeProperty(prop.copy(is_staging = false, targetingParameters = prop.targetingParameters.map { it.copy(value = "porcodio") }))
        (ds.fetchProperties() as Either.Right).r.first().is_staging.assertFalse()

        val res = (ds.fetchProperties() as Either.Right).r

        res.first().targetingParameters.size.assertEquals(3)

    }

}