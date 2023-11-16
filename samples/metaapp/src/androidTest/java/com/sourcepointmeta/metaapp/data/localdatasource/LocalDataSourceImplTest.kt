package com.sourcepointmeta.metaapp.data.localdatasource

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.core.Either
import kotlinx.coroutines.runBlocking
import org.junit.After
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
        propertyName = "prop1",
        accountId = 1,
        gdprPmId = 1212L,
        ccpaPmId = 1313L,
        usnatPmId = 1313L,
        is_staging = false,
        targetingParameters = tp,
        timeout = 1,
        authId = null,
        messageLanguage = "ENGLISH",
        pmTab = "DEFAULT",
        statusCampaignSet = setOf(StatusCampaign("prop1", CampaignType.GDPR, true)),
        campaignsEnv = CampaignsEnv.STAGE,
        propertyId = 111
    )

    private val prop2 = prop1.copy(propertyName = "prop2", accountId = 2, timeout = 2)

    private val db by lazy { createDb(appContext) }
    private val ds by lazy { LocalDataSource.create(db) }

    @Before
    fun setup() = runBlocking<Unit> {
        ds.deleteAll()
    }

    @After
    fun cleanUp() = runBlocking<Unit> {
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
    fun GIVEN_a_targetingparameter_SAVE_it_into_the_DB() = runBlocking<Unit> {
        val gdprState =
            StatusCampaign(propertyName = prop1.propertyName, campaignType = CampaignType.GDPR, enabled = true)
        val ccpaState =
            StatusCampaign(propertyName = prop1.propertyName, campaignType = CampaignType.CCPA, enabled = false)
        val prop3 = prop1.copy(statusCampaignSet = setOf(gdprState, ccpaState))
        ds.storeOrUpdateProperty(prop3)
        val sut = (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
        sut.statusCampaignSet.first { it.campaignType == CampaignType.GDPR }.enabled.assertTrue()
        sut.statusCampaignSet.first { it.campaignType == CampaignType.CCPA }.enabled.assertFalse()

        val prop4 =
            prop3.copy(statusCampaignSet = setOf(gdprState.copy(enabled = false), ccpaState.copy(enabled = true)))
        ds.storeOrUpdateProperty(prop4)
        val sut1 = (ds.fetchPropertyByName(prop3.propertyName) as Either.Right).r
        sut1.statusCampaignSet.first { it.campaignType == CampaignType.GDPR }.enabled.assertFalse()
        sut1.statusCampaignSet.first { it.campaignType == CampaignType.CCPA }.enabled.assertTrue()
    }

    @Test
    fun GIVEN_a_property_update_its_info() = runBlocking<Unit> {
        val p = prop1.copy(authId = "athu")
        repeat(2) { ds.storeOrUpdateProperty(p) }
        (ds.propertyCount() as Either.Right).r.assertEquals(1)
        ds.storeOrUpdateProperty(p.copy(gdprPmId = 111L, ccpaPmId = 222L, authId = "auth"))
        (ds.propertyCount() as Either.Right).r.assertEquals(1)
        val sut = (ds.fetchPropertyByName(prop1.propertyName) as Either.Right).r
        sut.run {
            gdprPmId.assertEquals(111L)
            ccpaPmId.assertEquals(222L)
            authId.assertEquals("auth")
        }
    }
}
