package com.sourcepointmeta.metaapp.data.localdatasource

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.* // ktlint-disable
import com.sourcepointmeta.metaapp.core.Either
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LocalDataSourceImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    private val tp = listOf(
        TargetingParam("test", "key1", "val1"),
        TargetingParam("test", "key2", "val2"),
        TargetingParam("test", "key3", "val3"),
    )

    private val prop1 = Property(
        account_id = 1,
        property_name = "prop1",
        property_id = 1,
        pm_id = "",
        auth_Id = null,
        message_language = null,
        pm_tab = null,
        is_staging = false,
        targetingParameters = tp
    )

    private val prop2 = prop1.copy(property_name = "prop2", account_id = 2, property_id = 2)

    private val db by lazy { createDb(appContext) }
    private val ds by lazy { LocalDataSource.create(db) }

    @Before
    fun setup() = runBlocking<Unit> {
        ds.deleteAll()
    }

    @Test
    fun property_CRUD() = runBlocking<Unit> {
        (ds.fetchProperties() as Either.Right).r.size.assertEquals(0)
        (ds.fetchTargetingParams(prop1.property_name) as Either.Right).r.size.assertEquals(0)
        ds.storeOrUpdateProperty(prop1)
        ds.storeOrUpdateProperty(prop2)
        (ds.fetchTargetingParams(prop1.property_name) as Either.Right).r.size.assertEquals(3)
        (ds.fetchProperties() as Either.Right).r.size.assertEquals(2)
        val p = (ds.fetchPropertyByName(prop1.property_name) as Either.Right).r
        p.run {
            targetingParameters.size.assertEquals(3)
            auth_Id.assertNull()
            is_staging.assertFalse()
        }
        ds.storeOrUpdateProperty(p.copy(auth_Id = "authId", is_staging = true))
        val p2 = (ds.fetchPropertyByName(prop1.property_name) as Either.Right).r
        p2.run {
            auth_Id.assertNotNull()
            is_staging.assertTrue()
        }
        ds.deleteByPropertyName(prop1.property_name)
        (ds.fetchProperties() as Either.Right).r.size.assertEquals(1)
    }

    @Test
    fun targetingParams_crud() = runBlocking<Unit> {
        ds.storeOrUpdateProperty(prop1)
        ds.storeOrUpdateProperty(prop2)
        val p = (ds.fetchPropertyByName(prop1.property_name) as Either.Right).r
        p.run {
            targetingParameters.size.assertEquals(3)
            targetingParameters.forEach { it.value.contains("val").assertTrue() }
        }
        ds.storeOrUpdateProperty(
            p.copy(
                is_staging = true,
                targetingParameters = p.targetingParameters.map { it.copy(value = "test") }
            )
        )
        (ds.fetchPropertyByName(prop1.property_name) as Either.Right).r
            .targetingParameters
            .forEach { it.value.contains("test").assertTrue() }
    }

    @Test
    fun db_test() = runBlocking<Unit> {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val db = createDb(appContext)

        val ds = LocalDataSource.create(db)

        ds.deleteAll()

        (ds.fetchTargetingParams("test") as? Either.Right)?.r!!.size.assertEquals(0)

        ds.storeOrUpdateProperty(prop1)
        val prop = (ds.fetchProperties() as Either.Right).r.first()
        ds.storeOrUpdateProperty(
            prop.copy(
                is_staging = true,
                targetingParameters = prop.targetingParameters.map { it.copy(value = "porcodio") }
            )
        )
        (ds.fetchProperties() as Either.Right).r.first().is_staging.assertTrue()
        ds.storeOrUpdateProperty(
            prop.copy(
                is_staging = false,
                targetingParameters = prop.targetingParameters.map { it.copy(value = "porcodio") }
            )
        )
        (ds.fetchProperties() as Either.Right).r.first().is_staging.assertFalse()

        val res = (ds.fetchProperties() as Either.Right).r

        res.first().targetingParameters.size.assertEquals(3)
    }
}
