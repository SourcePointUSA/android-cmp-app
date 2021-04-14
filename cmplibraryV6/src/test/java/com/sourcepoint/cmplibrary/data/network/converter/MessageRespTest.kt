package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.network.util.CampaignEnv
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

class MessageRespTest {

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
}
