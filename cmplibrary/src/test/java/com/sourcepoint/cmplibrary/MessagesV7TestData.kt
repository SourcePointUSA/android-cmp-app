package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.model.v7.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.util.Env

internal val messagesParamReq = MessagesParamReq(
    metadata = """ {"gdpr":{"targetingParams":{},"groupPmId":null},"ccpa":{"targetingParams":{"location":"US"},"groupPmId":null}} """,
    nonKeyedLocalState = "",
    body = "",
    env = Env.PROD,
    propertyHref = "tests.unified-script.com",
    accountId = 22,
    authId = null,
    propertyId = 17801
)
