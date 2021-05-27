package com.sourcepointmeta.metaapp.data.localdatasource

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.core.Either
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LocalDataSourceImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    private val tp = listOf<MetaTargetingParam>(
        MetaTargetingParam("test", CampaignType.GDPR, "key1", "val1"),
        MetaTargetingParam("test", CampaignType.GDPR, "key2", "val2"),
        MetaTargetingParam("test", CampaignType.GDPR, "key3", "val3"),
    )

    private val prop1 = Property(
        accountId = 1,
        propertyName = "prop1",
        propertyId = 1,
        pmId = "",
        authId = null,
        messageLanguage = null,
        pmTab = null,
        is_staging = false,
        targetingParameters = tp,
        statusCampaign = StatusCampaign("prop1"),
        messageType = "App"
    )

    private val prop2 = prop1.copy(propertyName = "prop2", accountId = 2, propertyId = 2)

    private val db by lazy { createDb(appContext) }
    private val ds by lazy { LocalDataSource.create(db) }

    @Before
    fun setup() = runBlocking<Unit> {
        ds.deleteAll()
    }

    @Test
    fun property_CRUD() = runBlocking<Unit> {
        (ds.fetchProperties() as Either.Right).r.size.assertEquals(0)
        (ds.fetchTargetingParams(prop1.propertyName) as Either.Right).r.size.assertEquals(0)
        ds.storeOrUpdateProperty(prop1)
        ds.storeOrUpdateProperty(prop2)
        (ds.fetchTargetingParams(prop1.propertyName) as Either.Right).r.size.assertEquals(3)
        (ds.fetchProperties() as Either.Right).r.size.assertEquals(2)
        val p = (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
        p.run {
            targetingParameters.size.assertEquals(3)
            authId.assertNull()
            is_staging.assertFalse()
        }
        ds.storeOrUpdateProperty(p.copy(authId = "authId", is_staging = true))
        val p2 = (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
        p2.run {
            authId.assertNotNull()
            is_staging.assertTrue()
        }
        ds.deleteByPropertyName(prop1.propertyName)
        (ds.fetchProperties() as Either.Right).r.size.assertEquals(1)
    }

    @Test
    fun targetingParams_crud() = runBlocking<Unit> {
        ds.storeOrUpdateProperty(prop1)
        ds.storeOrUpdateProperty(prop2)
        val p = (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
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
        (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
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

    @Test
    fun GIVEN_a_targeringparameter_SAVE_it_into_the_DB() = runBlocking<Unit> {
        val prop3 = prop1.copy(statusCampaign = StatusCampaign(propertyName = prop1.propertyName, gdprEnabled = true))
        ds.storeOrUpdateProperty(prop3)
        val sut = (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
        sut.statusCampaign.gdprEnabled.assertTrue()
        sut.statusCampaign.ccpaEnabled.assertFalse()

        val prop4 = prop3.copy(statusCampaign = StatusCampaign(propertyName = prop1.propertyName, gdprEnabled = false, ccpaEnabled = true))
        ds.storeOrUpdateProperty(prop4)
        val sut1 = (ds.fetchPropertyByName(prop3.propertyName) as Either.Right).r
        sut1.statusCampaign.gdprEnabled.assertFalse()
        sut1.statusCampaign.ccpaEnabled.assertTrue()
    }
}
