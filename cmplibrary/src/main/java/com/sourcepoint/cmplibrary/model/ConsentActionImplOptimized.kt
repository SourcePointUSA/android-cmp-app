package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.models.SPAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

@Serializable
data class ConsentActionImplOptimized(
    override val actionType: ActionType,
    override val choiceId: String? = null,
    override val consentLanguage: String? = MessageLanguage.ENGLISH.value,
    override val customActionId: String? = null,
    val legislation: CampaignType,
    val localPmId: String?,
    val name: String?,
    val pmId: String?,
    val pmTab: String? = null,
    override val requestFromPm: Boolean,
    @SerialName("saveAndExitVariables") val saveAndExitVariablesOptimized: JsonObject = JsonObject(mapOf()),
    @SerialName("pubData") override val pubData2: JsonObject = JsonObject(mapOf()),
    override val privacyManagerId: String? = null,
    override val messageId: String,
    override val pmUrl: String? = null
) : ConsentAction {
    override val pubData: JSONObject
        get() = JSONObject(pubData2)

    override val campaignType: CampaignType
        get() = legislation

    override val saveAndExitVariables: JSONObject
        get() = JSONObject(saveAndExitVariablesOptimized)

    fun toCore(): SPAction = SPAction(
        type = actionType.toCore(),
        campaignType = campaignType.toCore(),
        messageId = messageId,
        pmPayload = saveAndExitVariablesOptimized,
        encodablePubData = pubData2,
    )
}
