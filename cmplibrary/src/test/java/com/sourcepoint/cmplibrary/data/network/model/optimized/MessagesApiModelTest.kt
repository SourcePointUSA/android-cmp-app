package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignReqImpl
import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
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
        val list = listOf(
            CampaignReqImpl(
                targetingParams = emptyList(),
                campaignsEnv = CampaignsEnv.PUBLIC,
                campaignType = CampaignType.GDPR,
                groupPmId = null
            )
        )

        val body = createGetMessagesRequestBody(
            accountId = 22,
            gdprConsentStatus = cs.consentStatusData?.gdpr?.consentStatus,
            propertyHref = "tests.unified-script.com",
            campaigns = list,
            ccpaConsentStatus = null,
            consentLanguage = null,
            campaignEnv = CampaignsEnv.STAGE,
            localState = JsonObject(mapOf()),
        )

        (body["accountId"] as JsonPrimitive).content.assertEquals("22")
        (body["campaignEnv"] as JsonPrimitive).content.assertEquals("stage")
        (body["propertyHref"] as JsonPrimitive).content.assertEquals("https://tests.unified-script.com")
        (body["hasCSP"] as JsonPrimitive).content.assertEquals("false")
        body.contains("campaigns").assertTrue()
    }

    @Test
    fun `GIVEN IncludeData verify the output`() {
        val mess = "v7/messagesObjSwitchOrder.json".file2String()
        val message = JsonConverter.converter.decodeFromString<MessagesResp>(mess)
        message.campaignList[0] as CCPA
        message.campaignList[1] as GDPR
    }
}
