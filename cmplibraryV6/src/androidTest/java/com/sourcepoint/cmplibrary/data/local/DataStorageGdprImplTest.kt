package com.sourcepoint.cmplibrary.data.local

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.uitestutil.assertEquals
import com.example.uitestutil.jsonFile2String
import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.converter.toCCPA
import com.sourcepoint.cmplibrary.data.network.converter.toGDPR
import com.sourcepoint.cmplibrary.util.Either
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DataStorageGdprImplTest {

    private val appContext by lazy { InstrumentationRegistry.getInstrumentation().targetContext }

    @Test
    fun `GIVEN_an_Gdpr_object_STORE_it_into_the_local_data_storage`() {

        val unifiedMess = JSON.std.mapFrom("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String())
        val gdpr = (unifiedMess["gdpr"] as DeferredMap).toGDPR()!!

        val sut = DataStorageGdpr.create(appContext)
        sut.saveGdpr(gdpr)
        val res = sut.getGdpr()

        (res as Either.Right).r.run {
            uuid.assertEquals(gdpr.uuid)
            meta.assertEquals(gdpr.meta)
            gdprApplies.assertEquals(gdpr.gdprApplies)
            userConsent!!.let { uc ->
                uc.acceptedCategories.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.acceptedCategories.sortedBy { it.hashCode() })
                uc.acceptedVendors.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.acceptedVendors.sortedBy { it.hashCode() })
                uc.legIntCategories.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.legIntCategories.sortedBy { it.hashCode() })
                uc.specialFeatures.sortedBy { it.hashCode() }.assertEquals(gdpr.userConsent!!.specialFeatures.sortedBy { it.hashCode() })
            }
        }
    }

    @Test
    fun `GIVEN_an_Ccpa_object_STORE_it_into_the_local_data_storage`() {

        val unifiedMess = JSON.std.mapFrom("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String())
        val ccpa = (unifiedMess["ccpa"] as DeferredMap).toCCPA()!!

        val sut = DataStorageCcpa.create(appContext)
        sut.saveCcpa(ccpa)
        val res = sut.getCcpa()

        (res as Either.Right).r.run {
            uuid.assertEquals(ccpa.uuid)
            meta.assertEquals(ccpa.meta)
            ccpaApplies.assertEquals(ccpa.ccpaApplies)
            userConsent.let { uc ->
                uc.rejectedCategories.sortedBy { it.hashCode() }.assertEquals(ccpa.userConsent!!.rejectedCategories.sortedBy { it.hashCode() })
                uc.rejectedVendors.sortedBy { it.hashCode() }.assertEquals(ccpa.userConsent!!.rejectedVendors.sortedBy { it.hashCode() })
                uc.status.assertEquals(ccpa.userConsent.status)
                uc.uspstring.assertEquals(ccpa.userConsent.uspstring)
            }
        }
    }
}
