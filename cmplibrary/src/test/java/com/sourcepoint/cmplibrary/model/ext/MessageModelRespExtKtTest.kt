package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.util.file2String
import org.junit.Test

class MessageModelRespExtKtTest {

    @Test
    fun `GIVEN a message body request RETURN the related object`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".file2String().toUnifiedMessageRespDto()
        unifiedMess.run {
            localState.assertEquals("""{"gdpr":{"mmsCookies":["_sp_v1_uid=1:205:744ddc28-691d-412c-bdc8-8b41a58a0303","_sp_v1_data=2:3284:1618474707:0:1:0:1:0:0:_:-1","_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RyiuOUbIyqFWKBQCq7errDgAAAA%3D%3D","_sp_v1_opt=1:","_sp_v1_stage=","_sp_v1_csv=null","_sp_v1_lt=1:"],"uuid":"73c123e2-a66b-47ee-9a7f-b3d7413be960","propertyId":4122,"messageId":13201},"ccpa":{"mmsCookies":["_sp_v1_uid=1:743:d4b85270-f8c1-4fa3-9f70-4f0ba74528ef","_sp_v1_data=2:3286:1618474707:0:1:0:1:0:0:_:-1","_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RyiuOUbIyqFWKBQCq7errDgAAAA%3D%3D","_sp_v1_opt=1:","_sp_v1_stage=","_sp_v1_csv=null","_sp_v1_lt=1:"],"uuid":"3d4264ae-3eef-4cd5-9064-10dc336e05dd","dnsDisplayed":true,"propertyId":4122,"messageId":13203}}""")
            campaigns.size.assertEquals(2)
            campaigns.find { it.type == CampaignType.GDPR.name }.assertNotNull()
            campaigns.find { it.type == CampaignType.CCPA.name }.assertNotNull()
        }
    }
}
