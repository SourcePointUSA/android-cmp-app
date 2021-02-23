package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.util.* // ktlint-disable
import kotlin.collections.ArrayList

class JsonConverterExtKtTest {

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
