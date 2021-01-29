package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.gdpr_cmplibrary.ActionTypes
import org.json.JSONObject

data class ConsentAction(
    val choiceId: String?,
    val privacyManagerId: String?,
    val pmTab: String?,
    val requestFromPm: Boolean,
    val saveAndExitVariables: JSONObject,
    val consentLanguage: String?,
    val actionType: ActionTypes
)
