package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.file2List
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class JsonConverterImplTest {

    private val sut = JsonConverter.create()

    @Test
    fun `GIVEN return a string RETURN a Right(MessageResp)`() {
    }

    @Test
    fun `GIVEN return a string RETURN a Left(MessageResp)`() {
    }

    @Test
    fun `GIVEN return a string RETURN a Right(ConsentAction) dataset 1`() {
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
    fun `GIVEN return a string RETURN a Right(ConsentAction) dataset 2`() {
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
            println("========= TEST N. $index ============")
            when (val output = sut.toConsentAction(s)) {
                is Either.Left -> throw output.t
                is Either.Right -> {}
            }
        }
    }
}
