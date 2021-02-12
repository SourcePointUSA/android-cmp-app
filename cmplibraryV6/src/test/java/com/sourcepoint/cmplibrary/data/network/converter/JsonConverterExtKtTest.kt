package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import org.junit.Test

class JsonConverterExtKtTest {

    @Test
    fun `GIVEN a Gdpr json string PARSE to Gdpr Obj`() {
        val unifiedMess = JSON.std.mapFrom("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String())
        val gdpr = (unifiedMess["gdpr"] as DeferredMap).toGDPR()!!
        gdpr.run {
            gdprApplies.assertEquals(false)
            userConsent.assertNotNull()
            message.assertNotNull()
            meta.assertNotNull()
            uuid.assertEquals("a42f93fc-282c-422d-89f2-841e04d9217f")
        }
    }

    @Test
    fun `GIVEN a Ccpa json string PARSE to Ccpa Obj`() {
        val unifiedMess = JSON.std.mapFrom("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String())
        val ccpa = (unifiedMess["ccpa"] as DeferredMap).toCCPA()!!
        ccpa.run {
            ccpaApplies.assertEquals(true)
            userConsent.assertNotNull()
            message.assertNotNull()
            meta.assertNotNull()
            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
        }
    }
}
