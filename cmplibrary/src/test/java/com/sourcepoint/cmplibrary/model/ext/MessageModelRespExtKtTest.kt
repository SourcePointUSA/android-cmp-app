package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.data.network.converter.localStateTest
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.toTreeMap
import com.sourcepoint.cmplibrary.util.file2String
import org.json.JSONObject
import org.junit.Test

class MessageModelRespExtKtTest {

    @Test
    fun `GIVEN a message body request RETURN the related object`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()
        unifiedMess.run {
            localState.let { JSONObject(it).toTreeMap() }.assertEquals(localStateTest)
            campaigns.size.assertEquals(2)
            campaigns.find { it.type == CampaignType.GDPR.name }.assertNotNull()
            campaigns.find { it.type == CampaignType.CCPA.name }.assertNotNull()
        }
    }
}
