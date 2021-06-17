package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
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
                  "location":"EU"
                },
                "ccpa": {
                  "location":"EU"
                }
              },
              "consentLanguage": "EN",
              "requestUUID": "test",
              "includeData": {
                "messageMetaData": {},
                "localState": {
                  "type": "string"
                },
                "TCData": {}
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
