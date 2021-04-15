package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.exception.Legislation
import okhttp3.HttpUrl
import org.json.JSONObject

internal data class CampaignModel(
    val message: JSONObject,
    val messageMetaData: JSONObject,
    val type: Legislation,
    val url: HttpUrl
)
