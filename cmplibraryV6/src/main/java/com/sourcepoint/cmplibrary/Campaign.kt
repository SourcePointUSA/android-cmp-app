package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.model.Categories
import com.sourcepoint.cmplibrary.data.network.model.GdprReq
import com.sourcepoint.cmplibrary.data.network.model.MessageReq

data class Campaign(
    @JvmField val accountId: Int,
    @JvmField val propertyId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)

fun Campaign.toMessageReq(): MessageReq {
    return MessageReq(
        requestUUID = "test",
        categories = Categories(
            GdprReq(
                accountId = accountId,
                propertyId = propertyId,
                propertyHref = "https://tcfv2.mobile.webview"
            )
        )
    )
}
