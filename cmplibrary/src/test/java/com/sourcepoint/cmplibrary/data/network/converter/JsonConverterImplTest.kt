package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.model.NativeMessageResp
import com.sourcepoint.cmplibrary.model.UnifiedMessageResp
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.toConsentAction
import com.sourcepoint.cmplibrary.model.toTreeMap
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
    fun `GIVEN a native_message_resp RETURN a Right(NativeMessageResp)`() {
        val json = "native_message_resp.json".file2String()
        val testMap = JSONObject(json).toTreeMap()

        val output: Either<NativeMessageResp> = sut.toNativeMessageResp(json)
        (output as Either.Right).r.also { m ->
            m.msgJSON.get("name").assertEquals("GDPR Native Message")
            (m.msgJSON.get("title") as JSONObject).get("text").assertEquals("Personalised Ads")
            (m.msgJSON.get("body") as JSONObject).get("text").assertEquals("GDPR - Lorem ipsum dolor sit er elit lamet, consectetaur cillium adipisicing pecu, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Nam liber te conscient to factor tum poen legum odioque civiuda.")
        }
    }

    @Test
    fun `GIVEN an action sequence RETURN a (ConsentAction)`() {
        "action/gdpr_first_layer_accept_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.ACCEPT_ALL)
        "action/gdpr_first_layer_show_option.json".file2String().toConsentAction().actionType.assertEquals(ActionType.SHOW_OPTIONS)
        "action/gdpr_pm_accept_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.ACCEPT_ALL)
        "action/gdpr_pm_reject_all.json".file2String().toConsentAction().actionType.assertEquals(ActionType.REJECT_ALL)
        "action/gdpr_pm_save_and_exit.json".file2String().toConsentAction().actionType.assertEquals(ActionType.SAVE_AND_EXIT)
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
}
