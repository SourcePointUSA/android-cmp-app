package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.toBodyRequest
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.uwMessDataTest
import org.json.JSONObject
import org.junit.Test

class MessageModelReqExtKtTest {

    private val testJsonObject = JSONObject(
        """
            {
              "accountId": 1,
              "propertyHref": "http://com.test",
              "campaigns": {
                "gdpr": {
                  "targetingParams": "{\"location\":\"EU\"}",
                  "campaignEnv": "stage"
                },
                "ccpa": {
                  "targetingParams": "{\"location\":\"US\"}",
                  "campaignEnv": "stage"
                }
              },
              "consentLanguage": "EN",
              "requestUUID": "test",
              "includeData": {
                "customVendorsResponse": {
                  "type": "RecordString"
                },
                "messageMetaData": {
                  "type": "RecordString"
                },
                "localState": {
                  "type": "RecordString"
                },
                "TCData": {
                  "type": "RecordString"
                }
              }
            }
        """.trimIndent()
    )

    @Test
    fun `GIVEN a UnifiedMessageRequest object RETURN stringify request Json obj`() {
        val sut = JSONObject(uwMessDataTest.toBodyRequest())
        sut.toTreeMap().assertEquals(testJsonObject.toTreeMap())
    }
}
