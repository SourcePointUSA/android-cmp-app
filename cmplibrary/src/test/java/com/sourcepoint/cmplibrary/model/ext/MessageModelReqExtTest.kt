package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.model.toJsonObject
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.uwMessDataTest
import org.junit.Test

class MessageModelReqExtTest {

    @Test
    fun `GIVEN a UnifiedMessageRequest RETURN a JSONObject request`() {
        val sut = uwMessDataTest.toJsonObject()
        sut.toTreeMap().apply {
            getFieldValue<String>("requestUUID").assertEquals("test")
            getFieldValue<String>("propertyHref").assertEquals("http://com.test")
            getFieldValue<Int>("accountId").assertEquals(1)
            getFieldValue<String>("consentLanguage").assertEquals("EN")
        }
    }

    @Test
    fun `GIVEN a UnifiedMessageRequest CONTAINS a campaigns list`() {
        val sut = uwMessDataTest.toJsonObject()
        sut.toTreeMap().apply {
            getMap("campaigns")?.size.assertEquals(2)
            getFieldValue<String>("campaignEnv").assertEquals("stage")
            getMap("campaigns")?.also { map ->
                map.getMap("ccpa")?.getMap("targetingParams")?.getFieldValue<String>("location").assertEquals("US")
                map.getMap("gdpr")?.getMap("targetingParams")?.getFieldValue<String>("location").assertEquals("EU")
            }
        }
    }

    @Test
    fun `GIVEN a UnifiedMessageRequest CONTAINS a IncludeData obj`() {
        val sut = uwMessDataTest.toJsonObject()
        sut.toTreeMap().apply {
            getMap("includeData").assertNotNull()
        }
    }
}
