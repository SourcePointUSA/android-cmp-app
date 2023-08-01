package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class GetChoiceApiModelTest {

    @Test
    fun `GIVEN a ChoiceResp RETURN the parsed obj`() {
        val getChoiceJson = "v7/get-choice-consent-all.json".file2String()
        val choiceResp = JsonConverter.converter.decodeFromString<ChoiceResp>(getChoiceJson)
        JsonConverter.converter.encodeToJsonElement(choiceResp)
    }
}
