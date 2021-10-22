package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import org.json.JSONObject
import org.junit.Test

class JsonToMapExtKtTest {

    @Test
    fun `GIVEN a consent object RETURN a JSON`() {

        val map: Map<String, GDPRPurposeGrants?> = mapOf(
            "5fbe6f050d88c7d28d765d47" to GDPRPurposeGrants(true, mapOf("60657acc9c97c400122f21f3" to true, "60657acc9c97c400122f21fa" to true)),
            "5fbe6f090d88c7d28d765e1e" to GDPRPurposeGrants(true, mapOf("60657acc9c97c400122f21f3" to true)),
            "5fbe6f0a0d88c7d28d765e25" to GDPRPurposeGrants(false, mapOf("60657acc9c97c400122f220c" to false, "60657acc9c97c400122f2212" to true))
        )

        val res = map.toJSONObjGrant().toTreeMap()

        val tester = JSONObject(
            """
            {
              "5fbe6f050d88c7d28d765d47": {
                "granted": true,
                "purposeGrants": {
                  "60657acc9c97c400122f21f3": true,
                  "60657acc9c97c400122f21fa": true,
                }
              },
              "5fbe6f090d88c7d28d765e1e": {
                "granted": true,
                "purposeGrants": {
                  "60657acc9c97c400122f21f3": true
                }
              },
              "5fbe6f0a0d88c7d28d765e25": {
                "granted": false,
                "purposeGrants": {
                  "60657acc9c97c400122f220c": false,
                  "60657acc9c97c400122f2212": true
                }
              }
            }
            """.trimIndent()
        ).toTreeMap()

        res.assertEquals(tester)
    }
}
