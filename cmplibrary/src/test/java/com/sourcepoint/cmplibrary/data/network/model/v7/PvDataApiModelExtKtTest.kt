package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.create
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class PvDataApiModelExtKtTest {

    @Test
    fun `GIVEN the PvData body parameters RETURN a request body input`() {
        val testMapMSS = JSONObject("v7/messages.json".file2String())
        val messResp = (JsonConverter.create().toMessagesResp(testMapMSS.toString()) as Either.Right).r
        val sut = toPvDataBody(
            messages = messResp,
            accountId = 22,
            siteId = 111,
            gdprApplies = false,
            ccpaUuid = "1234",
            gdprUuid = "6789"
        )
        sut.toTreeMap().run {
            getMap("gdpr")!!.let { g ->
                g.getFieldValue<Boolean>("applies")!!.assertFalse()
                g.getFieldValue<String>("uuid")!!.assertEquals("6789")
                g.getFieldValue<Long>("accountId")!!.assertEquals(22)
                g.getFieldValue<Long>("siteId")!!.assertEquals(111)
                g.getFieldValue<String>("consentStatus")!!.assertEquals("string")
                g.getFieldValue<String>("euconsent")!!.assertEquals("CPe--UAPe--UAAGABCENCfCgAAAAAHAAAAYgAAAMZgAgMZADCgAQGMhwAIDGRIAEBjIA.YAAAAAAAAAAA")
                g.getFieldValue<String>("pubData")!!.assertEquals("string")
                g.getFieldValue<Int>("msgId")!!.assertEquals(521357)
                g.getFieldValue<Int>("categoryId")!!.assertEquals(1)
                g.getFieldValue<Int>("subCategoryId")!!.assertEquals(5)
                g.getFieldValue<String>("prtnUUID")!!.assertEquals("acc10281-503d-4ce7-b303-62683fada039")
                g.getFieldValue<Int>("sampleRate")!!.assertEquals(1)
            }
            getMap("ccpa")!!.let { c ->
                c.getFieldValue<Boolean>("applies")!!.assertTrue()
                c.getFieldValue<String>("uuid")!!.assertEquals("1234")
                c.getFieldValue<Long>("accountId")!!.assertEquals(22)
                c.getFieldValue<Long>("siteId")!!.assertEquals(111)
                c.getFieldValue<String>("consentStatus")!!.assertEquals("string")
                c.getFieldValue<String>("pubData")!!.assertEquals("string")
                c.getFieldValue<Int>("messageId")!!.assertEquals(704632)
                c.getFieldValue<Int>("sampleRate")!!.assertEquals(1)
            }
        }
    }
}
