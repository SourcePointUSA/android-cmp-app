package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.toAcceptedCategories
import com.sourcepoint.cmplibrary.data.network.model.toConsentAction
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class ConsentRespExtKtTest {

    @Test
    fun `GIVEN an action sequence RETURN a (ConsentAction)`() {
        "action/gdpr_first_layer_accept_all.json".file2String()
            .toConsentAction().actionType.assertEquals(ActionType.ACCEPT_ALL)
        "action/gdpr_first_layer_show_option.json".file2String()
            .toConsentAction().actionType.assertEquals(ActionType.SHOW_OPTIONS)
        "action/gdpr_pm_accept_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.ACCEPT_ALL)
        "action/gdpr_pm_reject_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.REJECT_ALL)
        "action/gdpr_pm_save_and_exit.json".file2String()
            .toConsentAction().actionType.assertEquals(ActionType.SAVE_AND_EXIT)
    }

    @Test
    fun `GIVEN a grant object RETURN an AcceptedCategories All true`() {
        val obj: Map<String, Map<String, Boolean>> = JSONObject(jsonGrantAllTrue).toTreeMap().map {
            Pair(
                it.key,
                ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
            )
        }
            .toMap()
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
        val obj: Map<String, Map<String, Boolean>> = JSONObject(jsonGrantAll1).toTreeMap().map {
            Pair(
                it.key,
                ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
            )
        }
            .toMap()
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
        val obj: Map<String, Map<String, Boolean>> = JSONObject(jsonGrantAll2).toTreeMap().map {
            Pair(
                it.key,
                ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
            )
        }
            .toMap()
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
        val obj: Map<String, Map<String, Boolean>> = JSONObject(jsonGrantAll3).toTreeMap().map {
            Pair(
                it.key,
                ((it.value as? Map<String, Any?>)?.get("purposeGrants") as? Map<String, Boolean>) ?: emptyMap()
            )
        }
            .toMap()
        val l: List<String> = obj.toAcceptedCategories().sorted()
        val tester = listOf(
            "60b65857619abe242bed971e",
            "608bad96d08d3112188e0e59"
        ).sorted()
        l.assertEquals(tester)
    }

    @Test
    fun `GIVEN a CCPA consent RETURN a consent object`() {
        val ccpaConsent = """
            {
              "dateCreated": "2021-10-11T14:34:08.288Z",
              "newUser": false,
              "rejectedAll": false,
              "rejectedCategories": [],
              "rejectedVendors": [],
              "signedLspa": false,
              "status": "consentedAll",
              "uspstring": "1---",
              "applies": true,
              "uuid": "1234"
            }
        """
        val test: CCPAConsentInternal = JsonConverter.converter.decodeFromString(CCPAConsentInternal.serializer(), ccpaConsent)

        test.run {
            uspstring.assertEquals("1YNN")
            status!!.name.assertEquals("consentedAll")
            rejectedCategories.size.assertEquals(0)
            rejectedVendors.size.assertEquals(0)
            uuid.assertEquals("1234")
            applies.assertTrue()
        }
    }

    @Test
    fun `GIVEN a CCPA consent with not empty rejectedCategories and rejectedVendors RETURN a consent object`() {
        val ccpaConsent = """
            {
              "dateCreated": "2021-10-11T14:34:08.288Z",
              "newUser": false,
              "rejectedAll": false,
              "rejectedCategories": ["rejectedCategory0", "rejectedCategory1"],
              "rejectedVendors": ["rejectedVendor0"],
              "signedLspa": false,
              "status": "consentedAll",
              "uspstring": "1---"
            }
        """
        val test: CCPAConsentInternal = JsonConverter.converter.decodeFromString(CCPAConsentInternal.serializer(), ccpaConsent)
        test.run {
            rejectedCategories.size.assertEquals(2)
            rejectedCategories[0].assertEquals("rejectedCategory0")
            rejectedCategories[1].assertEquals("rejectedCategory1")
            rejectedVendors.size.assertEquals(1)
            rejectedVendors[0].assertEquals("rejectedVendor0")
        }
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
