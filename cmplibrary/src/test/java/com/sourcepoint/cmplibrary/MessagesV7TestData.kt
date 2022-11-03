package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.v7.MessagesParamReq
import com.sourcepoint.cmplibrary.data.network.util.Env
import kotlinx.serialization.decodeFromString

internal val messagesParamReq = MessagesParamReq(
    metadataArg = JsonConverter.converter.decodeFromString(""" {"gdpr":{"targetingParams":{},"groupPmId":null},"ccpa":{"targetingParams":{"location":"US"},"groupPmId":null}} """),
    nonKeyedLocalState = "",
    body = "",
    env = Env.PROD,
    propertyHref = "tests.unified-script.com",
    accountId = 22,
    authId = null,
    propertyId = 17801
)
