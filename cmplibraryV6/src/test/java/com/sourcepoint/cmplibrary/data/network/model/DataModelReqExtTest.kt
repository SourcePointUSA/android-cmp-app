package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.TargetingParam
import org.junit.Test

class DataModelReqExtTest {

    private val req: MessageReq = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                targetingParams = Array(1){
                    TargetingParam("location", "EU")
                }.toJsonObjStringify(),
                campaignEnv = CampaignEnv.STAGE
            ),
            ccpa = CcpaReq(
                targetingParams = Array(1){
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
            contains("22").assertTrue()
            contains("https://tcfv2.mobile.webview").assertTrue()
            contains("EU").assertTrue()
            contains("GDPR").assertTrue()
        }

        ccpa.run {
            contains("22").assertTrue()
            contains("https://tcfv2.mobile.webview").assertTrue()
            contains("US").assertTrue()
            contains("CCPA").assertTrue()
        }
    }

    @Test
    fun `GIVEN a CampaignReq generate a JSONObject`() {
        val sut = req.campaigns.ccpa!!.toJsonObject().toString()

        sut.run {
            contains("22").assertTrue()
            contains("https://tcfv2.mobile.webview").assertTrue()
            contains("US").assertTrue()
            contains("CCPA").assertTrue()
        }
    }

    @Test
    fun `GIVEN a TargetingParams generate a JSONObject`() {
        val sut = req.campaigns.ccpa!!.targetingParams

        sut.run {
            contains("US").assertTrue()
            contains("CCPA").assertTrue()
        }
    }
}
