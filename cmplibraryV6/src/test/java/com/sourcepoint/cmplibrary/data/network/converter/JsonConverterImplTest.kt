package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.data.network.model.MessageResp
import com.sourcepoint.cmplibrary.data.network.model.NativeMessageResp
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.file2List
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class JsonConverterImplTest {

    private val sut = JsonConverter.create()

    @Test
    fun `GIVEN a string RETURN a Right(MessageResp)`() {
        val json = "unified_wrapper/full_resp.json".file2String()
        val output: Either<MessageResp> = sut.toMessageResp(json)
        (output as? Either.Left)?.let { throw it.t }
        (output as Either.Right).r.also { m ->
            m.ccpa.assertNull()
            m.gdpr.also { g ->
                g!!.uuid.assertEquals("144f3899-7887-445a-92fa-9a80a6fc8b5d")
                g.GDPRUserConsent.also { u ->
                    u.acceptedCategories.size.assertEquals(0)
                    u.acceptedVendors.size.assertEquals(0)
                    u.legIntCategories.size.assertEquals(2)
                    u.specialFeatures.size.assertEquals(0)
                    u.euconsent.assertEquals("CPAlTsBPAlTsBAGABCENBKCgAAAAAEIAAAYgAAAAPAAEAAAA.YAAAAAAAAAAA")
                }
                g.meta.contains("_sp_v1_uid=1:969:1346b52a-bfaa-4215-b54d-7bb787823f39").assertEquals(true)
            }
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
                is Either.Right -> {}
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
}
