package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.TestUtilGson.Companion.jsonFile2String
import com.sourcepoint.cmplibrary.data.network.model.toUnifiedMessageRespDto
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.Ccpa
import com.sourcepoint.cmplibrary.model.Gdpr
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import org.junit.Test

class JsonConverterExtKtTest {

    @Test
    fun `GIVEN a unified response json string PARSE to UnifiedMessageResp1230 Obj`() {
        val unifiedMess = "unified_wrapper_resp/response_gdpr_and_ccpa.json".jsonFile2String().toUnifiedMessageRespDto()

        unifiedMess.run {
            campaigns.size.assertEquals(2)
            propertyPriorityData.toTreeMap().assertEquals(propertyPriorityDataTest.toTreeMap())
            localState.let { JSONObject(it).toTreeMap() }.assertEquals(localStateTest)
        }

        val gdpr = unifiedMess.campaigns[0] as Gdpr
        val ccpa = unifiedMess.campaigns[1] as Ccpa

        gdpr.run {
            type.assertEquals(CampaignType.GDPR.name)
            applies.assertTrue()
            userConsent.also {
                it.tcData.assertNotNull()
                it.euconsent.contains("CPEpDOrPEpDOrHIABCENBVCgAAAAAH_AAAYgAAAOQA").assertTrue()
            }
        }

        ccpa.run {
            type.assertEquals(CampaignType.CCPA.name)
            applies.assertFalse()
            userConsent.also {
                it.rejectedCategories.size.assertEquals(0)
                it.rejectedVendors.size.assertEquals(0)
                it.status!!.name.assertEquals("rejectedNone")
                it.uspstring.assertEquals("1---")
            }
        }
    }
}

private val propertyPriorityDataTest = JSONObject(
    """
        {
          "stage_message_limit": 1,
          "site_id": 4122,
          "public_campaign_type_priority": [
            1,
            2
          ],
          "multi_campaign_enabled": true,
          "stage_campaign_type_priority": [
            2,
            1,
            2,
            1
          ],
          "public_message_limit": 3
        }
    """.trimIndent()
)

val localStateTest = JSONObject("""{"gdpr":{"mmsCookies":["_sp_v1_uid=1:205:744ddc28-691d-412c-bdc8-8b41a58a0303","_sp_v1_data=2:3284:1618474707:0:1:0:1:0:0:_:-1","_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RyiuOUbIyqFWKBQCq7errDgAAAA%3D%3D","_sp_v1_opt=1:","_sp_v1_stage=","_sp_v1_csv=null","_sp_v1_lt=1:"],"uuid":"73c123e2-a66b-47ee-9a7f-b3d7413be960","propertyId":4122,"messageId":13201},"ccpa":{"mmsCookies":["_sp_v1_uid=1:743:d4b85270-f8c1-4fa3-9f70-4f0ba74528ef","_sp_v1_data=2:3286:1618474707:0:1:0:1:0:0:_:-1","_sp_v1_ss=1:H4sIAAAAAAAAAItWqo5RyiuOUbIyqFWKBQCq7errDgAAAA%3D%3D","_sp_v1_opt=1:","_sp_v1_stage=","_sp_v1_csv=null","_sp_v1_lt=1:"],"uuid":"3d4264ae-3eef-4cd5-9064-10dc336e05dd","dnsDisplayed":true,"propertyId":4122,"messageId":13203}}""").toTreeMap()
