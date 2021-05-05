package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.Ccpa
import com.sourcepoint.cmplibrary.model.Gdpr
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.model.ext.toUnifiedMessageRespDto
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.util.* // ktlint-disable
import kotlin.collections.ArrayList

class JsonConverterExtKtTest {

    @Test
    fun `GIVEN a unified response json string PARSE to UnifiedMessageResp1230 Obj`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()

        unifiedMess.run {
            campaigns.size.assertEquals(2)
            propertyPriorityData.toTreeMap().assertEquals(propertyPriorityDataTest.toTreeMap())
            localState.assertEquals(localStateTest)
        }

        val gdpr = unifiedMess.campaigns[0] as Gdpr
        val ccpa = unifiedMess.campaigns[1] as Ccpa

        gdpr.run {
            type.assertEquals(CampaignType.GDPR.name)
            applies.assertTrue()
            userConsent.also {
                it.tcData.assertNotNull()
                it.euconsent.contains("CPEpDOrPEpDOrHIABCENBVCgAAAAAH_AAAYgAAAOQA").assertTrue()
            }
        }

        ccpa.run {
            type.assertEquals(CampaignType.CCPA.name)
            applies.assertFalse()
            userConsent.also {
                it.rejectedCategories.size.assertEquals(0)
                it.rejectedVendors.size.assertEquals(0)
                it.status.assertEquals("rejectedNone")
                it.signedLspa.assertFalse()
                it.uspstring.assertEquals("")
            }
        }
    }

//    @Test
//    fun `GIVEN a Gdpr json string PARSE to Gdpr Obj`() {
//        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
//
//        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr
//        gdpr.run {
//            gdprApplies.assertEquals(false)
//            userConsent.assertNotNull()
//            message.assertNotNull()
//            meta.assertNotNull()
//            uuid.assertEquals("a42f93fc-282c-422d-89f2-841e04d9217f")
//        }
//
//        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
//        ccpa.run {
//            ccpaApplies.assertEquals(true)
//            userConsent.assertNotNull()
//            message.assertNotNull()
//            meta.assertNotNull()
//            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
//        }
//    }

//    @Test
//    fun `GIVEN a Ccpa json string PARSE to Ccpa Obj`() {
//        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
//        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
//        ccpa.run {
//            ccpaApplies.assertEquals(true)
//            userConsent.assertNotNull()
//            message.assertNotNull()
//            meta.assertNotNull()
//            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
//        }
//    }

//    @Test
//    fun `GIVEN a consent Ccpa json string PARSE to CcpaConsent Obj`() {
//        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
//        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
//        ccpa.run {
//            ccpaApplies.assertEquals(true)
//            userConsent.assertNotNull()
//            message.assertNotNull()
//            meta.assertNotNull()
//            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
//        }
//    }

    @Test
    fun `GIVEN a Gdpr user consent json PARSE to GdprConsent Obj`() {
        val gdprConsent = JSONObject("unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String())
//        val gdprConsent = JSONObject("consent_resp/consent_accept_all.json".jsonFile2String())
//        val map = jsonToMap(gdprConsent)
        val map = jsonToMap(gdprConsent)
        val map1 = gdprConsent.toTreeMap()
        map.assertEquals(map1)
//        val gdpr = (unifiedMess["gdpr"] as DeferredMap).toGDPR()!!
//        gdpr.run {
//            gdprApplies.assertEquals(false)
//            userConsent.assertNotNull()
//            message.assertNotNull()
//            meta.assertNotNull()
//            uuid.assertEquals("a42f93fc-282c-422d-89f2-841e04d9217f")
//        }
    }

    fun jsonToMap(json: JSONObject): Map<String?, Any?>? {
        var retMap: Map<String?, Any?> = TreeMap()
        if (json !== JSONObject.NULL) {
            retMap = toMap(json)
        }
        return retMap
    }

    fun toMap(`object`: JSONObject): Map<String?, Any?> {
        val map: MutableMap<String?, Any?> = TreeMap()
        val keysItr = `object`.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value = `object`[key]
            if (value is JSONArray) {
                value = toList(value)
            } else if (value is JSONObject) {
                value = toMap(value)
            }
            map[key] = value
        }
        return map
    }

    fun toList(array: JSONArray): List<Any> {
        val list: MutableList<Any> = ArrayList()
        for (i in 0 until array.length()) {
            var value = array[i]
            if (value is JSONArray) {
                value = toList(value)
            } else if (value is JSONObject) {
                value = toMap(value)
            }
            list.add(value)
        }
        return list
    }

    /**
     * ======================================
     */

//    fun JSONObject.toTreeMap(): Map<String, Any?>? {
//        var map: Map<String, Any?> = TreeMap()
//        if (this !== JSONObject.NULL) {
//            return toMap1(this)
//        }
//        return map
//    }
//
//    fun toMap1(jsonObj: JSONObject): Map<String, Any?> {
//        val map: MutableMap<String, Any?> = TreeMap()
//        val keysItr = jsonObj.keys()
//        while (keysItr.hasNext()) {
//            val key = keysItr.next()
//            var value = jsonObj[key]
//            when (value) {
//                is JSONArray -> value = toList1(value)
//                is JSONObject -> value = toMap1(value)
//            }
//            map[key] = value
//        }
//        return map
//    }
//
//    fun toList1(array: JSONArray): List<Any> {
//        val list: MutableList<Any> = ArrayList()
//        for (i in 0 until array.length()) {
//            var value = array[i]
//            when (value) {
//                is JSONArray -> value = toList1(value)
//                is JSONObject -> value = toMap1(value)
//            }
//            list.add(value)
//        }
//        return list
//    }
}

private val propertyPriorityDataTest = JSONObject(
    """
        {
          "stage_message_limit": 1,
          "site_id": 4122,
          "public_campaign_type_priority": [
            1,
            2
          ],
          "multi_campaign_enabled": true,
          "stage_campaign_type_priority": [
            2,
            1,
            2,
            1
          ],
          "public_message_limit": 3
        }
    """.trimIndent()
)

private val localStateTest = "{\"gdpr\":{\"mmsCookies\":[\"_sp_v1_uid=1:205:744ddc28-691d-412c-bdc8-8b41a58a0303\",\"_sp_v1_data=2:3284:1618474707:0:1:0:1:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RyiuOUbIyqFWKBQCq7errDgAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"uuid\":\"73c123e2-a66b-47ee-9a7f-b3d7413be960\",\"propertyId\":4122,\"messageId\":13201},\"ccpa\":{\"mmsCookies\":[\"_sp_v1_uid=1:743:d4b85270-f8c1-4fa3-9f70-4f0ba74528ef\",\"_sp_v1_data=2:3286:1618474707:0:1:0:1:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RyiuOUbIyqFWKBQCq7errDgAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"uuid\":\"3d4264ae-3eef-4cd5-9064-10dc336e05dd\",\"dnsDisplayed\":true,\"propertyId\":4122,\"messageId\":13203}}"
