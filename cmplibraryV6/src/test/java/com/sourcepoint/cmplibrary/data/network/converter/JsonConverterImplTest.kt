package com.sourcepoint.cmplibrary.data.network.converter

import com.fasterxml.jackson.jr.ob.JSON
import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.core.layout.json.Style
import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.InvalidResponseWebMessageException
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.file2List
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class JsonConverterImplTest {

    private val sut = JsonConverter.create()

    @Test
    fun `GIVEN an empty string RETURN a Left(MessageResp)`() {
        val json = "{}"
        val output: Either<MessageResp> = sut.toMessageResp(json)
        (output as Either.Left).t.message!!.contains("We have [0] inst. of Message.").assertEquals(true)
    }

    @Test
    fun `GIVEN a json with GDPR as applied legislation RETURN a Right(MessageResp)`() {
        val json = "unified_wrapper/message_in_gdpr.json".file2String()
        val output: Either<MessageResp> = sut.toMessageResp(json)
        (output as? Either.Left)?.let { throw it.t }
        val gdprMess = (output as Either.Right).r
        gdprMess.legislation.assertEquals(Legislation.GDPR)
        gdprMess.message.also { mess ->
            mess["site_id"].assertEquals(7639)
            mess["language"].assertEquals("en")
            mess["message_json"].assertNotNull()
            mess["message_choice"].assertNotNull()
            mess["categories"].assertNotNull()
        }
        gdprMess.legislation.assertEquals(Legislation.GDPR)
        gdprMess.message.also { mess ->
            mess["site_id"].assertEquals(7639)
            mess["language"].assertEquals("en")
            mess["message_json"].assertNotNull()
            mess["message_choice"].assertNotNull()
            mess["categories"].assertNotNull()
        }
        (gdprMess.userConsent as GDPRUserConsent).run {
            acceptedCategories.size.assertEquals(0)
            acceptedVendors.size.assertEquals(0)
            legIntCategories.size.assertEquals(2)
            specialFeatures.size.assertEquals(0)
            euconsent.assertEquals("CPAlTsBPAlTsBAGABCENBKCgAAAAAEIAAAYgAAAAPAAEAAAA.YAAAAAAAAAAA")
        }
    }

    @Test(expected = InvalidResponseWebMessageException::class)
    fun `GIVEN a CCPA json resp with an invalid CCPAStatus THROWS an exception`() {
        val json = "unified_wrapper/ccpa_mess_wrong_status.json".file2String()
        val left = sut.toMessageResp(json) as Either.Left
        throw left.t
    }

    @Test
    fun `GIVEN a json with CCPA as applied legislation RETURN a Right(MessageResp)`() {
        val json = "unified_wrapper/ccpa_mess_correct.json".file2String()
        val output: Either<MessageResp> = sut.toMessageResp(json)
        (output as? Either.Left)?.let { throw it.t }
        val ccpaMess = (output as Either.Right).r
        ccpaMess.legislation.assertEquals(Legislation.CCPA)
        ccpaMess.message.also { mess ->
            mess["site_id"].assertEquals(7639)
            mess["language"].assertEquals("en")
            mess["message_json"].assertNotNull()
            mess["message_choice"].assertNotNull()
            mess["categories"].assertNotNull()
        }
        ccpaMess.message.also { mess ->
            mess["site_id"].assertEquals(7639)
            mess["language"].assertEquals("en")
            mess["message_json"].assertNotNull()
            mess["message_choice"].assertNotNull()
            mess["categories"].assertNotNull()
        }
        (ccpaMess.userConsent as CCPAUserConsent).run {
            rejectedCategories.size.assertEquals(0)
            rejectedVendors.size.assertEquals(0)
            status.assertEquals(CCPAStatus.REJECTED_NONE)
            uspstring.assertEquals("test")
        }
    }

    @Test
    fun `GIVEN a native_message_resp RETURN a Right(NativeMessageResp)`() {
        val json = "native_message_resp.json".file2String()
        val output: Either<NativeMessageResp> = sut.toNativeMessageResp(json)
        (output as Either.Right).r.also { m ->
            m.msgJSON.get("name").assertEquals("GDPR Native Message")
            (m.msgJSON.get("title") as JSONObject).get("text").assertEquals("Personalised Ads")
            (m.msgJSON.get("body") as JSONObject).get("text").assertEquals("GDPR - Lorem ipsum dolor sit er elit lamet, consectetaur cillium adipisicing pecu, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Nam liber te conscient to factor tum poen legum odioque civiuda.")
        }
    }

    @Test
    fun `GIVEN a list of json RETURN a always Right(MessageResp)`() {
        val jsonList = "unified_wrapper/full_resp.txt".file2List()
        jsonList.forEachIndexed { index, s ->
            print("========= TEST toMessageResp [$index]: ")
            when (val output = sut.toMessageResp(s)) {
                is Either.Left -> throw output.t
                is Either.Right -> {
                }
            }
            println("success ============")
        }
    }

    @Test
    fun `GIVEN a string RETURN a Left(MessageResp)`() {
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
            it.privacyManagerId.assertNull()
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
            it.saveAndExitVariables.toString() assertEquals (JSONObject().toString())
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
        val nm = (sut.toNativeMessageDto(json) as Either.Right).r
        nm.name.assertEquals("GDPR Native Message")
        nm.title!!.also {
            it.text.assertEquals("Personalised Ads")
            checkStyle(it.style!!, "Arial", 28, "800", "#353331", "#fff")
        }
        nm.body!!.also {
            it.text!!.contains("GDPR - Lorem ipsum dolor").assertEquals(true)
            checkStyle(it.style!!, "Arial", 28, "800", "#353331", "#fff")
            JSON.std.asString(it.customFields).assertEquals("{}")
        }
        nm.actions!![0].run {
            choiceId!!.assertEquals(2956680)
            choiceType!!.assertEquals(11)
            checkStyle(style!!, "Arial", 18, "700", "#ffffff", "#ff0d00")
        }
        JSON.std.asString(nm.customFields).assertEquals("{}")
    }

    fun checkStyle(
        style: Style,
        fontFamily: String,
        fontSize: Int,
        fontWeight: String,
        color: String,
        backgroundColor: String
    ) {
        style.run {
            fontFamily.assertEquals(fontFamily)
            fontSize.assertEquals(fontSize)
            fontWeight.assertEquals(fontWeight)
            color.assertEquals(color)
            backgroundColor.assertEquals(backgroundColor)
        }
    }
}
