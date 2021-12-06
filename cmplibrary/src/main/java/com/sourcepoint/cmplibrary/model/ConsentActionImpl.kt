package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import org.json.JSONObject

interface ConsentAction {
    val actionType: ActionType
    val pubData: JSONObject
    val campaignType: CampaignType
    val customActionId: String?
}

internal data class ConsentActionImpl(
    override val campaignType: CampaignType,
    override val pubData: JSONObject = JSONObject(),
    override val actionType: ActionType,
    override val customActionId: String? = null,
    val requestFromPm: Boolean,
    val singleShotPM: Boolean = false,
    val saveAndExitVariables: JSONObject = JSONObject(),
    val pmTab: String? = null,
    val privacyManagerId: String? = null,
    val choiceId: String? = null,
    val consentLanguage: String? = MessageLanguage.ENGLISH.value,
    val thisContent: JSONObject = JSONObject()
) : ConsentAction

data class NativeConsentAction(
    val actionType: NativeMessageActionType,
    val campaignType: CampaignType,
    val privacyManagerId: String? = null
)

internal fun NativeConsentAction.toConsentAction() = ConsentActionImpl(
    actionType = ActionType.values().find { it.code == this.actionType.code } ?: failParam("toConsentAction"),
    campaignType = campaignType,
    requestFromPm = false
)
