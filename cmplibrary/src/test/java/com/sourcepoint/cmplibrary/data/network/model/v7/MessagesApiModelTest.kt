package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.encodeToJsonElement
import org.junit.Test

class MessagesApiModelTest {

    @Test
    fun `GIVEN a priority {1, 2, 5} GDPR is first`() {
        val mess = "v7/messagesObj.json".file2String()
        val message = JsonConverter.converter.decodeFromString<MessagesResp>(mess)
        message.campaignList[0] as GDPR
        message.campaignList[1] as CCPA

        JsonConverter.converter.encodeToJsonElement(message)
    }

    @Test
    fun `GIVEN a priority {1, 5, 2} CCPA is first`() {
        val mess = "v7/messagesObjSwitchOrder.json".file2String()
        val message = JsonConverter.converter.decodeFromString<MessagesResp>(mess)
        message.campaignList[0] as CCPA
        message.campaignList[1] as GDPR
    }

    @Test
    fun `GIVEN a ConsentStatus RETURN a messages body`() {

        val json = "v7/consent_status_with_auth_id.json".file2String()
        val cs = JsonConverter.converter.decodeFromString<ConsentStatusResp>(json)

        val body = getMessageBody(
            accountId = 22,
            cs = cs,
            propertyHref = "tests.unified-script.com"
        )

        println(body)
    }

    @Test
    fun `GIVEN IncludeData verify the output`() {
        val mess = "v7/messagesObjSwitchOrder.json".file2String()
        val message = JsonConverter.converter.decodeFromString<MessagesResp>(mess)
        message.campaignList[0] as CCPA
        message.campaignList[1] as GDPR
    }
}
