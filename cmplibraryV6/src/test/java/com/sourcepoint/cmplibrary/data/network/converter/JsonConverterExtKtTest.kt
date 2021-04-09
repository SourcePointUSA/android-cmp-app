package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.util.* // ktlint-disable
import kotlin.collections.ArrayList

class JsonConverterExtKtTest {

    @Test
    fun `GIVEN a unified response json string PARSE to UnifiedMessageResp1230 Obj`() {
        val unifiedMess = "unified_w_campaigns_list/campaigns_list.json".jsonFile2String().toUnifiedMessageRespDto1203()

        unifiedMess.run {
            campaigns.size.assertEquals(2)
            propertyPriorityData.toTreeMap().assertEquals(propertyPriorityDataTest.toTreeMap())
            localState.assertEquals(localStateTest)
        }

        val gdpr = unifiedMess.campaigns[0] as Gdpr1203
        val ccpa = unifiedMess.campaigns[1] as Ccpa1203

        gdpr.run {
            type.assertEquals(Legislation.GDPR.name)
            applies.assertTrue()
            userConsent.also {
                it.tcData.assertNotNull()
                it.euConsent.contains("CPD0nOZPD0nOZHIABCENBTCgAAAAAH").assertTrue()
            }
        }

        ccpa.run {
            type.assertEquals(Legislation.CCPA.name)
            applies.assertFalse()
            userConsent.also {
                it.rejectedCategories.size.assertEquals(0)
                it.rejectedVendors.size.assertEquals(0)
                it.status.assertEquals("rejectedNone")
                it.rejectedAll.assertFalse()
                it.signedLspa.assertFalse()
                it.uspstring.assertEquals("1---")
            }
        }
    }

    @Test
    fun `GIVEN a Gdpr json string PARSE to Gdpr Obj`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()

        val gdpr = unifiedMess.campaigns.find { it is Gdpr } as Gdpr
        gdpr.run {
            gdprApplies.assertEquals(false)
            userConsent.assertNotNull()
            message.assertNotNull()
            meta.assertNotNull()
            uuid.assertEquals("a42f93fc-282c-422d-89f2-841e04d9217f")
        }

        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        ccpa.run {
            ccpaApplies.assertEquals(true)
            userConsent.assertNotNull()
            message.assertNotNull()
            meta.assertNotNull()
            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
        }
    }

    @Test
    fun `GIVEN a Ccpa json string PARSE to Ccpa Obj`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        ccpa.run {
            ccpaApplies.assertEquals(true)
            userConsent.assertNotNull()
            message.assertNotNull()
            meta.assertNotNull()
            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
        }
    }

    @Test
    fun `GIVEN a consent Ccpa json string PARSE to CcpaConsent Obj`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()
        val ccpa = unifiedMess.campaigns.find { it is Ccpa } as Ccpa
        ccpa.run {
            ccpaApplies.assertEquals(true)
            userConsent.assertNotNull()
            message.assertNotNull()
            meta.assertNotNull()
            uuid.assertEquals("9c027fb5-9dde-4d07-9f79-bbb316489b9c")
        }
    }

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
      "site_id": 3949,
      "public_campaign_type_priority": [
        4,
        1,
        2,
        3
      ],
      "multi_campaign_enabled": false,
      "stage_campaign_type_priority": [],
      "public_message_limit": 3
    }
    """.trimIndent()
)

private val localStateTest = "{\"gdpr\":{\"mmsCookies\":[\"_sp_v1_uid=1:156:653c7e59-d8de-47af-b122-b8aaca1e8ef2\",\"_sp_v1_data=2:2356:1617027907:0:1:0:1:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_consent=1!-1:-1:-1:-1:-1:-1\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"uuid\":\"52ff72dc-c17b-4bf8-9128-4c5bb337d4e8\",\"propertyId\":3949,\"messageId\":12223},\"ccpa\":{\"mmsCookies\":[\"_sp_v1_uid=1:964:3a21519b-581b-4302-b517-2630f4907ac1\",\"_sp_v1_data=2:2358:1617027907:0:1:0:1:0:0:_:-1\",\"_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RKimOUbKKBjLyQAyD2lidGKVUEDOvNCcHyC4BK6iurVWKBQAW54XRMAAAAA%3D%3D\",\"_sp_v1_opt=1:\",\"_sp_v1_consent=1!-1:-1:-1:-1:-1:-1\",\"_sp_v1_stage=\",\"_sp_v1_csv=null\",\"_sp_v1_lt=1:\"],\"uuid\":\"7b85be47-75b3-4105-99af-0dd5497ca08f\",\"dnsDisplayed\":true,\"status\":\"rejectedNone\",\"propertyId\":3949,\"messageId\":12224}}"
