package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceTypeParam
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.toSPCampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.models.SPAction
import com.sourcepoint.mobile_core.models.SPActionType
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

interface ConsentAction {
    val actionType: ActionType
    val pubData: JSONObject
    val pubData2: JsonObject
    val campaignType: CampaignType
    val customActionId: String?
    val privacyManagerId: String?
    val choiceId: String?
    val requestFromPm: Boolean
    val saveAndExitVariables: JSONObject
    val consentLanguage: String?
}

internal data class ConsentActionImpl(
    override val campaignType: CampaignType,
    override val pubData: JSONObject = JSONObject(),
    override val pubData2: JsonObject = JsonObject(mapOf()),
    override val actionType: ActionType,
    override val customActionId: String? = null,
    override val privacyManagerId: String? = null,
    override val choiceId: String? = null,
    override val requestFromPm: Boolean,
    override val saveAndExitVariables: JSONObject = JSONObject(),
    override val consentLanguage: String? = MessageLanguage.ENGLISH.value,
    val saveAndExitVariablesOptimized: JsonObject = JsonObject(mapOf()),
    val pmTab: String? = null,
    val thisContent: JSONObject = JSONObject()
) : ConsentAction

internal fun ConsentActionImpl.privacyManagerTab(): PMTab {
    return pmTab?.let { pt -> PMTab.values().find { it.key == pt } }
        ?: PMTab.DEFAULT
}

internal fun ConsentActionImpl.toCoreSPAction(): SPAction {
    return SPAction(
        type = actionType.toSPActionType(),
        campaignType = campaignType.toSPCampaignType(),
        messageId = "TODO()",
        pmPayload = saveAndExitVariablesOptimized.toString(),
        encodablePubData = pubData.toJsonObject()
    )
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

internal fun ActionType.toSPActionType(): SPActionType = SPActionType.entries.first { it.type == this.code }
