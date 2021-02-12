package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import org.junit.Test

class MessageRespTest {

    val req = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview"
            ),
            ccpa = CcpaReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview"
            )
        )
    )

    @Test
    fun `tree`() {
        val uMessage = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String()
        val bean = JSON.std.mapFrom(uMessage)

        val r = (bean["gdpr"] as DeferredMap).toGDPR()
        bean.assertNotNull()
    }
}
