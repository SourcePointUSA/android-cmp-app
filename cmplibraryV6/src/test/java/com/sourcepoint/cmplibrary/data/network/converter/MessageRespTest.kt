package com.sourcepoint.cmplibrary.data.network.converter

import com.sourcepoint.cmplibrary.data.network.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.Legislation

class MessageRespTest {

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
}
