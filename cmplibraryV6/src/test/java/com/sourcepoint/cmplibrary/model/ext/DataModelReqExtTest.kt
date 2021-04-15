package com.sourcepoint.cmplibrary.model.ext

import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.model.Campaigns
import com.sourcepoint.cmplibrary.model.CcpaReq
import com.sourcepoint.cmplibrary.model.GdprReq
import com.sourcepoint.cmplibrary.model.MessageReq
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import org.junit.Test

class DataModelReqExtTest {

    private val req: MessageReq = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                targetingParams = Array(1) {
                    TargetingParam("location", "EU")
                }.toJsonObjStringify(),
                campaignEnv = CampaignEnv.STAGE
            ),
            ccpa = CcpaReq(
                targetingParams = Array(1) {
                    TargetingParam("location", "US")
                }.toJsonObjStringify(),
                campaignEnv = CampaignEnv.STAGE
            )
        )
    )

    @Test
    fun `GIVEN a MessageReq generate a JSONObject`() {
        val reqObj = req.toJsonObject().toString()
        val gdpr = req.campaigns.gdpr!!.toJsonObject().toString()
        val ccpa = req.campaigns.ccpa!!.toJsonObject().toString()

        reqObj.contains("test").assertTrue()

        gdpr.run {
            contains("stage").assertTrue()
            contains("EU").assertTrue()
        }

        ccpa.run {
            contains("stage").assertTrue()
            contains("US").assertTrue()
        }
    }

    @Test
    fun `GIVEN a CampaignReq generate a JSONObject`() {
        val sut = req.campaigns.ccpa!!.toJsonObject().toString()

        sut.run {
            contains("stage").assertTrue()
            contains("US").assertTrue()
        }
    }

    @Test
    fun `GIVEN a TargetingParams generate a JSONObject`() {
        val sut = req.campaigns.ccpa!!.targetingParams

        sut.run {
            contains("US").assertTrue()
        }
    }
}
