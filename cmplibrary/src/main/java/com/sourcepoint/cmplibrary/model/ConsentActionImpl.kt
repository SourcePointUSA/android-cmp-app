package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.data.network.model.v7.ChoiceTypeParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

interface ConsentAction {
    val actionType: ActionType
    val pubData: JSONObject
    val pubData2: JsonObject
    val campaignType: CampaignType
    val customActionId: String?
}

internal data class ConsentActionImpl(
    override val campaignType: CampaignType,
    override val pubData: JSONObject = JSONObject(),
    override val pubData2: JsonObject = JsonObject(mapOf()),
    override val actionType: ActionType,
    override val customActionId: String? = null,
    val requestFromPm: Boolean,
    val singleShotPM: Boolean = false,
    val saveAndExitVariables: JSONObject = JSONObject(),
    val saveAndExitVariablesV7: JsonObject = JsonObject(mapOf()),
    val pmTab: String? = null,
    val privacyManagerId: String? = null,
    val choiceId: String? = null,
    val consentLanguage: String? = MessageLanguage.ENGLISH.value,
    val thisContent: JSONObject = JSONObject()
) : ConsentAction

internal fun ConsentActionImpl.privacyManagerTab(): PMTab {
    return pmTab?.let { pt -> PMTab.values().find { it.key == pt } }
        ?: PMTab.DEFAULT
}

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

internal fun ActionType.toChoiceTypeParam(): ChoiceTypeParam = when (this) {
    ActionType.ACCEPT_ALL -> ChoiceTypeParam.CONSENT_ALL
    ActionType.REJECT_ALL -> ChoiceTypeParam.REJECT_ALL
    else -> throw RuntimeException("ChoiceTypeParam doesn't match the ActionType")
}
