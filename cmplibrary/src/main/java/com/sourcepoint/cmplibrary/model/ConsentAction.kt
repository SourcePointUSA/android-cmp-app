package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import org.json.JSONObject

data class ConsentAction(
    val requestFromPm: Boolean,
    val saveAndExitVariables: JSONObject = JSONObject(),
    val pubData: JSONObject = JSONObject(),
    val actionType: ActionType,
    val campaignType: CampaignType,
    val pmTab: String? = null,
    val privacyManagerId: String? = null,
    val choiceId: String? = null,
    val consentLanguage: String? = MessageLanguage.ENGLISH.value,
    val thisContent: JSONObject = JSONObject()
)

data class NativeConsentAction(
    val actionType: NativeMessageActionType,
    val campaignType: CampaignType,
    val privacyManagerId: String? = null
)

fun NativeConsentAction.toConsentAction() = ConsentAction(
    actionType = ActionType.values().find { it.code == this.actionType.code } ?: failParam("toConsentAction"),
    campaignType = campaignType,
    requestFromPm = false
)
