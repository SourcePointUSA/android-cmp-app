package com.sourcepoint.cmplibrary.mobile_core

import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.util.extensions.toJsonObject
import com.sourcepoint.mobile_core.models.SPAction
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

object JSONObjectSerializer : KSerializer<JSONObject> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("JSONObject", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: JSONObject) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder) = JSONObject(decoder.decodeString())
}

@Serializable
data class SPConsentAction(
    override val actionType: ActionType,
    @Serializable(with = JSONObjectSerializer::class) override val pubData: JSONObject = JSONObject(),
    @Deprecated("use this.pubData instead", replaceWith = ReplaceWith("this.pubData"), level = DeprecationLevel.ERROR)
    override val pubData2: JsonObject = JsonObject(emptyMap()),
    override val campaignType: CampaignType,
    override val customActionId: String?,

    @Deprecated("this field is no longer in use and should be removed")
    override val privacyManagerId: String?,

    @Deprecated("this field is no longer in use and should be removed", level = DeprecationLevel.ERROR)
    override val choiceId: String?,

    @Deprecated("this field is no longer in use and should be removed")
    override val requestFromPm: Boolean,
    @Serializable(with = JSONObjectSerializer::class) override val saveAndExitVariables: JSONObject = JSONObject(),
    override val consentLanguage: String?,
    override val messageId: String,
    override val pmUrl: String?
) : ConsentAction {
    fun toCore(): SPAction = SPAction(
        type = actionType.toCore(),
        campaignType = campaignType.toCore(),
        messageId = messageId,
        pmPayload = saveAndExitVariables.toJsonObject(),
        encodablePubData = pubData.toJsonObject(),
    )
}
