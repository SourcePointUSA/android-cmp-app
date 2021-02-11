package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.model.*
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
    fun `parse response`() {
        val jsonContent = "unified_wrapper/full_resp.json".jsonFile2String()

        val json1 = JSON.std.asString(req)
        val ob = JSON.std.anyFrom(jsonContent)
        val map = JSON.std.mapFrom(jsonContent)
        val json = JSON.std.asString((map.get("gdpr") as DeferredMap).get("message"))

        val gdpr = (map["gdpr"] as DeferredMap)
        val uuid = (gdpr["uuid"] as? String)
        val message = JSON.std.asString(gdpr["message"])
        val userConsent = JSON.std.beanFrom(GDPRUserConsent::class.java, JSON.std.asString(gdpr["userConsent"] as DeferredMap))

        println()
    }
}
