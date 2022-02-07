package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.toBodyRequest
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.uwMessDataTest
import com.sourcepoint.cmplibrary.uwMessDataTestPubData
import org.json.JSONObject
import org.junit.Test

class MessageModelReqExtKtTest {

    @Test
    fun `GIVEN a UnifiedMessageRequest object RETURN stringify request Json obj`() {
        val sut = JSONObject(uwMessDataTest.toBodyRequest())
        sut.toTreeMap().assertEquals(testJsonObject.toTreeMap())
    }

    @Test
    fun `GIVEN a UnifiedMessageRequest object with pubData RETURN stringify request Json obj`() {
        val sut = JSONObject(uwMessDataTestPubData.toBodyRequest())
        sut.toTreeMap().assertEquals(testJsonObjectPubData.toTreeMap())
    }

    private val testJsonObject = JSONObject(
        """
            {
              "accountId": 1,
              "propertyHref": "http://com.test",
              "campaigns": {
                "gdpr": {
                  "targetingParams": {
                    "location": "EU"
                  }
                },
                "ccpa": {
                  "targetingParams": {
                    "location": "US"
                  }
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
              },
              "campaignEnv": "stage"
            }
        """.trimIndent()
    )

    private val testJsonObjectPubData = JSONObject(
        """
            {
              "accountId": 1,
              "propertyHref": "http://com.test",
              "pubData": {
                "key_1": true,
                "key_2": "test_pb",
                "key_3": 1
              },
              "campaigns": {
                "gdpr": {
                  "targetingParams": {
                    "location": "EU"
                  }
                },
                "ccpa": {
                  "targetingParams": {
                    "location": "US"
                  }
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
              },
              "campaignEnv": "stage"
            }
        """.trimIndent()
    )
}
