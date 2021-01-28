package com.sourcepoint.cmplibrary.data.network.converted

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.file2String
import org.junit.Test

class JsonConverterImplTest {

    private val sut = JsonConverter.create()

    @Test
    fun `GIVEN return a string RETURN a Right(UWResp)`() {
    }

    @Test
    fun `GIVEN return a string RETURN a Left(UWResp)`() {
    }

    @Test
    fun `GIVEN return a string RETURN a Right(ConsentAction) dataset 1`() {
        val json = "action_data_1.json".file2String()
        val output = sut.toConsentAction(json)
        (output as Either.Right).r.also {
            it.actionType.name.assertEquals("SHOW_OPTIONS")
            it.choiceId.assertEquals("3108753")
            it.consentLanguage.assertEquals("EN")
            it.pmSaveAndExitVariables.assertNull()
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
            it.pmSaveAndExitVariables.assertNull()
            it.requestFromPm.assertEquals(true)
            it.privacyManagerId.assertNull()
        }
    }
}
