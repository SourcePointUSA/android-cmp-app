package com.sourcepoint.cmplibrary.data.network.model

import com.sourcepoint.cmplibrary.model.ActionType
import org.json.JSONObject

data class ConsentAction(
    val choiceId: String?,
    val privacyManagerId: String?,
    val pmTab: String?,
    val requestFromPm: Boolean,
    val saveAndExitVariables: JSONObject,
    val consentLanguage: String?,
    val actionType: ActionType
)
