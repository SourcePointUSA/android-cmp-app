package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.data.network.converter.ActionTypeSerializer
import com.sourcepoint.cmplibrary.data.network.converter.CampaignTypeSerializer
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

@Serializable
data class ConsentActionImplV7(
    @Serializable(with = ActionTypeSerializer::class) override val actionType: ActionType,
    @SerialName("choiceId") val choiceId: String? = null,
    @SerialName("consentLanguage") val consentLanguage: String? = MessageLanguage.ENGLISH.value,
    @SerialName("customActionId") override val customActionId: String? = null,
    @Serializable(with = CampaignTypeSerializer::class) val legislation: CampaignType,
    @SerialName("localPmId") val localPmId: String?,
    @SerialName("name") val name: String?,
    @SerialName("pmId") val pmId: String?,
    @SerialName("pmTab") val pmTab: String? = null,
    @SerialName("requestFromPm") val requestFromPm: Boolean,
    @SerialName("saveAndExitVariables") val saveAndExitVariablesV7: JsonObject = JsonObject(mapOf()),
    @SerialName("singleShot") val singleShot: Boolean?,
    @SerialName("pubData") override val pubData2: JsonObject = JsonObject(mapOf()),
    @SerialName("singleShotPM") val singleShotPM: Boolean = false,
    @SerialName("privacyManagerId") val privacyManagerId: String? = null,
) : ConsentAction {
    override val pubData: JSONObject
        get() = JSONObject(pubData2)

    override val campaignType: CampaignType
        get() = legislation
}
