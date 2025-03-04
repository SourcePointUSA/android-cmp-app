package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
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
    val messageId: String
    val pmUrl: String?
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
    val thisContent: JSONObject = JSONObject(),
    override val messageId: String,
    override val pmUrl: String? = null
) : ConsentAction
