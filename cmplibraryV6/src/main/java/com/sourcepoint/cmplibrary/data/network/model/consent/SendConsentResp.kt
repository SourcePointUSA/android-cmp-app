package com.sourcepoint.cmplibrary.data.network.model.consent

import com.sourcepoint.cmplibrary.exception.Legislation
import org.json.JSONObject

data class ConsentResp(
    val content: JSONObject,
    val userConsent: String?,
    var legislation: Legislation? = null
)
