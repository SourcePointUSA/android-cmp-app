package com.sourcepoint.cmplibrary.data.network.converter

// import com.fasterxml.jackson.jr.ob.JSON
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.TargetingParam
import com.sourcepoint.cmplibrary.util.file2String
import org.junit.Test
import java.util.* // ktlint-disable

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

    internal val req = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                targetingParams = Array(1){
                    TargetingParam("location", "EU")
                }.toJsonObjStringify(),
                campaignEnv = CampaignEnv.STAGE
            ),
            ccpa = CcpaReq(
                targetingParams = Array(1){
                    TargetingParam("location", "US")
                }.toJsonObjStringify(),
                campaignEnv = CampaignEnv.STAGE
            )
        )
    )

    @Test
    fun `GIVEN an Request obj CHECK the output`() {

        val messReq = "message_req.json".file2String()

//        val expected = JSON.std.mapFrom(messReq).toSortedMap()
//        val sut = JSON.std.mapFrom(JSON.std.asString(req)).toSortedMap()
//
//        Assert.assertEquals(expected, sut)

//        expected.assertEquals(sut)
    }
}
