package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.model.*

data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

fun Campaign.toMessageReq(): MessageReq {

    return MessageReq(
        requestUUID = "test",
        campaigns = Campaigns(
            gdpr = GdprReq(
                accountId = 22,
                propertyId = 10589,
                propertyHref = "https://unified.mobile.demo",
            ),
            ccpa = CcpaReq(
                accountId = 22,
                propertyId = 10589,
                propertyHref = "https://unified.mobile.demo"
            )
        )
    )
}
