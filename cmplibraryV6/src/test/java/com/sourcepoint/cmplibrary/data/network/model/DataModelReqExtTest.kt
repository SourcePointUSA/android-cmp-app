package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.exception.Legislation
import org.junit.Test

class DataModelReqExtKtTest {

    private val req: MessageReq = MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview",
                targetingParams = TargetingParams(
                    legislation = Legislation.GDPR.name,
                    location = "EU"
                ).toJsonObjStringify()
            ),
            ccpa = CcpaReq(
                accountId = 22,
                propertyId = 7639,
                propertyHref = "https://tcfv2.mobile.webview",
                targetingParams = TargetingParams(
                    legislation = Legislation.CCPA.name,
                    location = "US"
                ).toJsonObjStringify()
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
            contains("7639").assertTrue()
            contains("https://tcfv2.mobile.webview").assertTrue()
            contains("EU").assertTrue()
            contains("GDPR").assertTrue()
        }

        ccpa.run {
            contains("22").assertTrue()
            contains("7639").assertTrue()
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
            contains("7639").assertTrue()
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
