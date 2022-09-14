package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.* //ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.data.network.model.v7.GdprMess
import com.sourcepoint.cmplibrary.data.network.model.v7.toJsonObject
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.*  //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import com.sourcepoint.cmplibrary.util.file2List
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class JsonConverterImplTest {

    private val sut = JsonConverter.create()

    @Test
    fun `GIVEN a response RETURN a Right(MessageResp)`() {

        val json = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output: UnifiedMessageResp = (sut.toUnifiedMessageResp(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN a response execute toUnifiedMessageResp and RETURN a Left object`() {

        val json = "[]" // invalid json
        val output: Throwable? = (sut.toUnifiedMessageResp(json) as? Either.Left)?.t
        output.assertNotNull()
    }

    @Test
    fun `GIVEN a native_message_resp RETURN a Right(NativeMessageResp)`() {
        val json = "native_message_resp.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output: Either<NativeMessageResp> = sut.toNativeMessageResp(json)
        (output as Either.Right).r.also { m ->
            m.msgJSON.get("name").assertEquals("GDPR Native Message")
            (m.msgJSON.get("title") as JSONObject).get("text").assertEquals("Personalised Ads")
            (m.msgJSON.get("body") as JSONObject).get("text")
                .assertEquals("GDPR - Lorem ipsum dolor sit er elit lamet, consectetaur cillium adipisicing pecu, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Nam liber te conscient to factor tum poen legum odioque civiuda.")
        }
    }

    @Test
    fun `GIVEN a ACCEPT_ALL consent body resp RETURN a right object`() {
        val json = "consent_resp/consent_accept_all.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toConsentAction(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN a REJECT_ALL consent body resp RETURN a right object`() {
        val json = "consent_resp/consent_reject_all.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toConsentAction(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN a SAVE_AND_EXIT consent body resp RETURN a right object`() {
        val json = "consent_resp/consent_save_and_exit.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toConsentAction(json) as Either.Right).r
        output.thisContent.toTreeMap().assertEquals(testMap)
    }

    @Test
    fun `GIVEN an invalid json EXECUTE toConsentResp and RETURN a Left object`() {
        val json = "{}"
        val testMap = JSONObject(json).toTreeMap()

        val output = (sut.toUnifiedMessageResp(json) as? Either.Left)
        output.assertNotNull()
    }

    @Test
    fun `GIVEN a dataset RETURN a Right(ConsentAction)`() {
        val json = "action_data_1.json".file2String()
        val output = sut.toConsentAction(json)
        (output as Either.Right).r.also {
            it.actionType.name.assertEquals("SHOW_OPTIONS")
            it.choiceId.assertEquals("3108753")
            it.consentLanguage.assertEquals("EN")
            it.saveAndExitVariables.toString() assertEquals (JSONObject().toString())
            it.requestFromPm.assertEquals(false)
            it.privacyManagerId.assertNotNull()
        }
    }

    @Test
    fun `GIVEN a dataset  1RETURN a Right(ConsentAction)`() {
        val json = "action_data_2.json".file2String()
        val output = sut.toConsentAction(json)
        (output as Either.Right).r.also {
            it.actionType.name.assertEquals("REJECT_ALL")
            it.choiceId.assertNull()
            it.consentLanguage.assertEquals("EN")
            it.requestFromPm.assertEquals(true)
            it.privacyManagerId.assertNull()
        }
    }

    @Test
    fun `GIVEN a bunch of ConsentAction json files RETURN always right`() {
        val jsonList = "consent_action_examples.txt".file2List()
        jsonList.forEachIndexed { index, s ->
            print("========= TEST toConsentAction [$index]: ")
            when (val output = sut.toConsentAction(s)) {
                is Either.Left -> throw output.t
                is Either.Right -> {
                }
            }
            println("success ============")
        }
    }

    @Test
    fun `GIVEN a native layout dataset  RETURN a Right(NativeMessageModel)`() {
        val json = "native_layout.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        val nm = (sut.toNativeMessageDto(json) as Either.Right).r
        nm.thisContent.assertEquals(testMap)
    }

    @Test
    fun `GIVEN a metadata body resp RETURN a Right(MetaDataResp)`() {
        val json = "v7/meta_data.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        val nm = (sut.toMetaDataRespResp(json) as Either.Right).r
        nm.thisContent.toTreeMap().assertEquals(testMap)
        nm.run {
            gdpr.also {
                it!!.additionsChangeDate.assertEquals("2022-05-11T21:05:16.262Z")
                it.getMessageAlways.assertFalse()
                it.legalBasisChangeDate.assertEquals("2022-05-11T21:05:25.600Z")
                it.version.assertEquals(27)
                it._id.assertEquals("5fa9a8fda228635eaf24ceb5")
                it.applies.assertTrue()
            }
            ccpa.also {
                it!!.applies.assertTrue()
            }
        }
    }

    @Test
    fun `GIVEN a consent_status body resp RETURN a Right(ConsentStatusResp)`() {
        val json = "v7/consent_status_with_auth_id.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        val nm = (sut.toConsentStatusResp(json) as Either.Right).r
        nm.thisContent.toTreeMap().assertEquals(testMap)
        nm.consentStatusData.gdprCS!!.run {
            addtlConsent.assertEquals("1~")
            grants.size.assertEquals(5)
            euconsent.assertEquals("CPeQ1MAPeQ1MAAGABCENCdCsAP_AAHAAAAYgGMwBAAMgA0AXmAxkDGYAIDGQCgkAMADIANAF5hQAIDGQ4AEBjIkACAxkVABAXmMgAgLzHQAwAMgA0AXmQgAgAZJQAgAMgLzKQAwAMgA0AXmA.YAAAAAAAAAAA")
            dateCreated.assertEquals("2022-08-25T20:56:38.551Z")
            gdprApplies.assertTrue()
            cookieExpirationDays.assertEquals(365)
            localDataCurrent.assertFalse()
            vendorListId.assertEquals("5fa9a8fda228635eaf24ceb5")
            uuid.assertEquals("69b29ebc-c358-4d7f-9220-38ca2f00125b_1_2_3_4_5_6_7_8_9_10")
        }
        nm.consentStatusData.ccpaCS!!.run {
            dateCreated.assertEquals("2022-08-25T20:56:39.010Z")
            newUser.assertFalse()
            consentedAll.assertFalse()
            rejectedCategories.size.assertEquals(0)
            rejectedVendors.size.assertEquals(0)
            rejectedAll.assertFalse()
            status!!.name.assertEquals("rejectedNone")
            signedLspa.assertFalse()
            uspstring.assertEquals("1YNN")
            gpcEnabled.assertFalse()
            uuid.assertEquals("e47e539d-41dd-442b-bb08-5cf52b1e33d4")
            ccpaApplies.assertTrue()
        }
    }

    @Test
    fun `GIVEN a consent_status without authId body resp RETURN a Right(ConsentStatusResp)`() {
        val json = "v7/consent_status_without_auth_id.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        // talk with Sid to fix the boolean-null value
        val nm = (sut.toConsentStatusResp(json) as Either.Right).r
        nm.thisContent.toTreeMap().assertEquals(testMap)
        nm.consentStatusData.gdprCS!!.run {
            addtlConsent.assertEquals("1~")
            grants.size.assertEquals(5)
            euconsent.assertEquals("CPeeA8APeeA8AAGABCENCeCgAAAAAHAAAAYgAAAMZgAgMZADCgAQGMhwAIDGRIAEBjIA.YAAAAAAAAAAA")
            dateCreated.assertEquals("2022-08-29T13:40:54.754Z")
            gdprApplies.assertTrue()
            cookieExpirationDays.assertEquals(365)
            localDataCurrent.assertFalse()
            vendorListId.assertEquals("5fa9a8fda228635eaf24ceb5")
            uuid.assertEquals("e47e539d-41dd-442b-bb08-5cf52b1e33d4")
        }
    }

    @Test
    fun `GIVEN a pv_data body resp RETURN a Right(PvDataResp)`() {
        val json = "v7/pv_data.json".file2String()
        val nm = (sut.toPvDataResp(json) as Either.Right).r
        nm.gdprPv!!.cookies[0].also {
            it.getString("key").assertEquals("consentUUID")
            it.getString("value").assertEquals("86ac1e2e-ef53-451a-9e65-27c51f95adb8")
            it.getInt("maxAge").assertEquals(31536000)
            it.getBoolean("shareRootDomain").assertFalse()
            it.getBoolean("session").assertFalse()
        }
    }

    @Test
    fun `GIVEN a message body resp RETURN a Right(MessagesResp)`() {
        val json = "v7/messages.json".file2String()
        val testMap = JSONObject(json).toTreeMap()
        val nm = (sut.toMessagesResp(json) as Either.Right).r
        (nm.campaigns[0] as GdprMess).run {
            val gdprTester = testMap.getList("campaigns")!![0]
            type.assertEquals(CampaignType.GDPR.name)
            message!!.toTreeMap().toString().assertEquals(gdprTester["message"].toString())
            dateCreated.assertEquals(gdprTester["dateCreated"])
            messageMetaData!!.toTreeMap().toString().assertEquals(gdprTester["messageMetaData"].toString())
            url.toString().assertEquals(gdprTester["url"].toString())
            messageSubCategory.assertEquals(MessageSubCategory.TCFv2)
            grants.size.assertEquals(5)
            consentStatusCS!!.toJsonObject().toTreeMap().toString().assertEquals(gdprTester["consentStatus"].toString())
            hasLocalData.assertFalse()
            addtlConsent.assertEquals(gdprTester["addtlConsent"].toString())
            euconsent.assertEquals(gdprTester["euconsent"].toString())
            customVendorsResponse!!.toTreeMap().toString().assertEquals(gdprTester["customVendorsResponse"].toString())
            childPmId.assertNull()
        }
    }
}
