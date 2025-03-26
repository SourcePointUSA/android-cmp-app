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
    val requestFromPm: Boolean?
    val saveAndExitVariables: JSONObject
    val consentLanguage: String?
    val messageId: String
    val pmUrl: String?
}
