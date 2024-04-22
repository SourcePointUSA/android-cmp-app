package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotEquals
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class IncludeDataKtTest {

    @Test
    fun `GIVEN an include object VERIFY that contains the right objects`() {
        buildIncludeData().apply {
            containsKey("categories")
            containsKey("GPPData")
            containsKey("webConsentPayload")
            containsKey("campaigns")
            containsKey("TCData")
        }
    }

    @Test
    fun `GIVEN an include object VERIFY that GPPData contains true`() {
        buildIncludeData()["GPPData"].assertEquals(JsonPrimitive(true))
    }

    @Test
    fun `GIVEN an include object VERIFY that GPPData contains an object`() {
        val param = IncludeDataGppParam("yes", "yes", "yes")
            .let { Json.encodeToJsonElement(it) }
        val wrongParam = IncludeDataGppParam("no", "no", "no")
            .let { Json.encodeToJsonElement(it) }
        buildIncludeData(gppDataValue = param)["GPPData"].assertEquals(param)
        buildIncludeData(gppDataValue = param)["GPPData"].assertNotEquals(wrongParam)
    }
}
