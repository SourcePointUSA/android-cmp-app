package com.sourcepoint.cmplibrary.data.network

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import org.junit.Test

class UWRespTest {

    val req = UWReq(
        requestUUID = "test",
        categories = Categories(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview"
            )
        )
    )

    @Test
    fun `parse response`() {
        val jsonContent = "unified_wrapper_resp.json".jsonFile2String()

        val json1 = JSON.std.asString(req)
        val ob = JSON.std.anyFrom(jsonContent)
        val map = JSON.std.mapFrom(jsonContent)
        val json = JSON.std.asString((map.get("gdpr") as DeferredMap).get("message"))

        val gdpr = (map["gdpr"] as DeferredMap)
        val uuid = (gdpr["uuid"] as? String)
        val message = JSON.std.asString(gdpr["message"])
        val userConsent = JSON.std.beanFrom(UserConsent::class.java, JSON.std.asString(gdpr["userConsent"] as DeferredMap))

        println()
    }
}
