package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.toAcceptedCategories
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import kotlinx.serialization.decodeFromString
import org.junit.Test

class ConsentStatusApiModelExtKtTest {

    @Test
    fun `GIVEN a grant object RETURN an AcceptedCategories All true`() {

        val obj = JsonConverter.converter
            .decodeFromString<Map<String, GDPRPurposeGrants>?>(jsonGrantAllTrue)!!
            .toList()
            .fold(mutableMapOf<String, Map<String, Boolean>>()) { acc, elem ->
                acc[elem.first] = elem.second.purposeGrants
                acc
            }

        val l: List<String> = obj.toAcceptedCategories().sorted()
        val tester = listOf(
            "608bad95d08d3112188e0e29",
            "608bad95d08d3112188e0e36",
            "608bad96d08d3112188e0e59",
            "60b65857619abe242bed971e",
            "608bad95d08d3112188e0e2f"
        ).sorted()
        l.assertEquals(tester)
    }

    @Test
    fun `GIVEN a grant object RETURN an AcceptedCategories t1`() {

        val obj = JsonConverter.converter
            .decodeFromString<Map<String, GDPRPurposeGrants>?>(jsonGrantAll1)!!
            .toList()
            .fold(mutableMapOf<String, Map<String, Boolean>>()) { acc, elem ->
                acc[elem.first] = elem.second.purposeGrants
                acc
            }

        val l: List<String> = obj.toAcceptedCategories().sorted()
        val tester = listOf(
            "608bad95d08d3112188e0e36",
            "608bad96d08d3112188e0e59",
            "60b65857619abe242bed971e",
            "608bad95d08d3112188e0e2f"
        ).sorted()
        l.assertEquals(tester)
    }

    @Test
    fun `GIVEN a grant object RETURN an AcceptedCategories t2`() {

        val obj = JsonConverter.converter
            .decodeFromString<Map<String, GDPRPurposeGrants>?>(jsonGrantAll2)!!
            .toList()
            .fold(mutableMapOf<String, Map<String, Boolean>>()) { acc, elem ->
                acc[elem.first] = elem.second.purposeGrants
                acc
            }

        val l: List<String> = obj.toAcceptedCategories().sorted()
        val tester = listOf(
            "608bad95d08d3112188e0e29",
            "608bad95d08d3112188e0e36",
            "60b65857619abe242bed971e",
            "608bad95d08d3112188e0e2f"
        ).sorted()
        l.assertEquals(tester)
    }

    @Test
    fun `GIVEN a grant object RETURN an AcceptedCategories t3`() {

        val obj = JsonConverter.converter
            .decodeFromString<Map<String, GDPRPurposeGrants>?>(jsonGrantAll3)!!
            .toList()
            .fold(mutableMapOf<String, Map<String, Boolean>>()) { acc, elem ->
                acc[elem.first] = elem.second.purposeGrants
                acc
            }

        val l: List<String> = obj.toAcceptedCategories().sorted()
        val tester = listOf(
            "60b65857619abe242bed971e",
            "608bad96d08d3112188e0e59"
        ).sorted()
        l.assertEquals(tester)
    }

    private val jsonGrantAll3 = """
        {
          "5e7ced57b8e05c485246cce0": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": true,
              "608bad95d08d3112188e0e36": true,
              "608bad96d08d3112188e0e59": true,
              "60b65857619abe242bed971e": true
            },
            "vendorGrant": true
          },
          "5f1b2fbeb8e05c306f2a1eb9": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": false,
              "608bad95d08d3112188e0e2f": true
            },
            "vendorGrant": false
          },
          "5ff4d000a228633ac048be41": {
            "purposeGrants": {
              "608bad95d08d3112188e0e2f": false,
              "608bad95d08d3112188e0e36": false
            },
            "vendorGrant": false
          }
        }
    """.trimIndent()

    private val jsonGrantAllTrue = """
        {
          "5e7ced57b8e05c485246cce0": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": true,
              "608bad95d08d3112188e0e36": true,
              "608bad96d08d3112188e0e59": true,
              "60b65857619abe242bed971e": true
            },
            "vendorGrant": true
          },
          "5f1b2fbeb8e05c306f2a1eb9": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": true,
              "608bad95d08d3112188e0e2f": true
            },
            "vendorGrant": true
          },
          "5ff4d000a228633ac048be41": {
            "purposeGrants": {
              "608bad95d08d3112188e0e2f": true,
              "608bad95d08d3112188e0e36": true
            },
            "vendorGrant": true
          }
        }
    """.trimIndent()

    private val jsonGrantAll2 = """
        {
          "5e7ced57b8e05c485246cce0": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": true,
              "608bad95d08d3112188e0e36": true,
              "608bad96d08d3112188e0e59": false,
              "60b65857619abe242bed971e": true
            },
            "vendorGrant": true
          },
          "5f1b2fbeb8e05c306f2a1eb9": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": true,
              "608bad95d08d3112188e0e2f": true
            },
            "vendorGrant": true
          },
          "5ff4d000a228633ac048be41": {
            "purposeGrants": {
              "608bad95d08d3112188e0e2f": true,
              "608bad95d08d3112188e0e36": true
            },
            "vendorGrant": true
          }
        }
    """.trimIndent()

    private val jsonGrantAll1 = """
        {
          "5e7ced57b8e05c485246cce0": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": false,
              "608bad95d08d3112188e0e36": true,
              "608bad96d08d3112188e0e59": true,
              "60b65857619abe242bed971e": true
            },
            "vendorGrant": true
          },
          "5f1b2fbeb8e05c306f2a1eb9": {
            "purposeGrants": {
              "608bad95d08d3112188e0e29": true,
              "608bad95d08d3112188e0e2f": true
            },
            "vendorGrant": true
          },
          "5ff4d000a228633ac048be41": {
            "purposeGrants": {
              "608bad95d08d3112188e0e2f": true,
              "608bad95d08d3112188e0e36": true
            },
            "vendorGrant": true
          }
        }
    """.trimIndent()
}
