package com.sourcepoint.cmplibrary.data.network.model.optimized.includeData

import com.sourcepoint.cmplibrary.assertEquals
import kotlinx.serialization.json.JsonPrimitive
import org.junit.Test

class IncludeDataKtTest {

    @Test
    fun `GIVEN an include object VERIFY that contains the right objects`() {
        buildIncludeData().apply {
            containsKey("categories")
            containsKey("translateMessage")
            containsKey("GPPData")
            containsKey("webConsentPayload")
            containsKey("campaigns")
            containsKey("TCData")
        }
    }

    @Test
    fun `GIVEN an include object VERIFY that GPPData contains true`() {
        buildIncludeData()["GPPData"].assertEquals(JsonPrimitive("true"))
    }

    @Test
    fun `GIVEN an include object VERIFY that GPPData contains an object`() {
        val param = IncludeDataGppParam("yes", "yes", "yes").encodeToString()
        buildIncludeData(gppDataValue = param)["GPPData"].assertEquals(JsonPrimitive(param))
    }
}
