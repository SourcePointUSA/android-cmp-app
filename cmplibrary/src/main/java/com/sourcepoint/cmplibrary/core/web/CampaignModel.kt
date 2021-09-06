package com.sourcepoint.cmplibrary.core.web

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.MessageSubCategory
import okhttp3.HttpUrl
import org.json.JSONObject

internal data class CampaignModel(
    val message: JSONObject,
    val messageMetaData: JSONObject,
    val type: CampaignType,
    val url: HttpUrl,
    val messageSubCategory : MessageSubCategory
)
