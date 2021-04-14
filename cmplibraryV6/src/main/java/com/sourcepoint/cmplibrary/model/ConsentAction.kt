package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import org.json.JSONObject

data class ConsentAction(
    val requestFromPm: Boolean,
    val saveAndExitVariables: JSONObject = JSONObject(),
    val pubData: JSONObject = JSONObject(),
    val actionType: ActionType,
    val legislation: Legislation,
    val pmTab: String? = null,
    val privacyManagerId: String? = null,
    val choiceId: String? = null,
    val consentLanguage: String? = MessageLanguage.ENGLISH.value,
)
