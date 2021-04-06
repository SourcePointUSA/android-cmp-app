package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

data class CampaignModel(
    val message: JSONObject,
    val messageMetaData: JSONObject,
    val type: Legislation
)
