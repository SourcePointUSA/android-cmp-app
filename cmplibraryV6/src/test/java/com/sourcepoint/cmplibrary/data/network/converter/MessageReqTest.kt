package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.fasterxml.jackson.jr.ob.impl.DeferredMap
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.model.*
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import java.util.*


class MessageReqTest {

    /*
     {
      "requestUUID": "test",
      "campaigns": {
        "gdpr": {
          "accountId": 22,
          "propertyId": 10589,
          "propertyHref": "https://unified.mobile.demo",
          "targetingParams": "{\"location\": \"GDPR\"}"
        },
        "ccpa": {
          "alwaysDisplayDNS": false,
          "accountId": 22,
          "propertyId": 10589,
          "propertyHref": "https://unified.mobile.demo",
          "targetingParams": "{\"location\": \"CCPA\"}"
        }
      }
    }
     */

    val req = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 10589,
                propertyHref = "https://unified.mobile.demo",
            ),
            ccpa = CcpaReq(
                accountId = 22,
                propertyId = 10589,
                propertyHref = "https://unified.mobile.demo"
            )
        )
    )

    @Test
    fun `GIVEN an Request obj CHECK the output`(){


        val messReq = "message_req.json".file2String()

        val expected = JSON.std.mapFrom(messReq).toSortedMap()
        val sut = JSON.std.mapFrom(JSON.std.asString(req)).toSortedMap()

        Assert.assertEquals(expected, sut)

//        expected.assertEquals(sut)


    }

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
