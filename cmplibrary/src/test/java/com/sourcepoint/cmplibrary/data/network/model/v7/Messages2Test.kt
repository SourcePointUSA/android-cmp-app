package com.sourcepoint.cmplibrary.data.network.model.v7

import com.sourcepoint.cmplibrary.util.file2String
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test

class Messages2Test {

    private val converter by lazy {
        Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            isLenient = true
            allowStructuredMapKeys = true
            prettyPrint = true
            prettyPrintIndent = "  "
            coerceInputValues = true
            useArrayPolymorphism = true
            allowSpecialFloatingPointValues = true
        }
    }

    @Test
    fun `GIVEN a priority {1, 2, 5} GDPR is first`() {
        val mess = "v7/messagesObj.json".file2String()
        val message = converter.decodeFromString<Messages2>(mess)
        message.campaignList[0] as Messages2.Campaigns.GDPR
        message.campaignList[1] as Messages2.Campaigns.CCPA
    }

    @Test
    fun `GIVEN a priority {1, 5, 2} CCPA is first`() {
        val mess = "v7/messagesObjSwitchOrder.json".file2String()
        val message = converter.decodeFromString<Messages2>(mess)
        message.campaignList[0] as Messages2.Campaigns.CCPA
        message.campaignList[1] as Messages2.Campaigns.GDPR
    }


}