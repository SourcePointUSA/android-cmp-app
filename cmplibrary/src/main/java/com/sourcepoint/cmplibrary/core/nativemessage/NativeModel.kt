package com.sourcepoint.cmplibrary.core.nativemessage

import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType.* // ktlint-disable

data class MessageStructure(
    val messageComponents: MessageComponents?,
    val campaignType: CampaignType
)

data class MessageComponents(
    val name: String,
    val title: NativeComponent?,
    val body: NativeComponent?,
    val actions: List<NativeAction> = emptyList(),
    val customFields: Map<String, String> = emptyMap()
)

data class NativeComponent(
    val text: String?,
    val style: NativeStyle?,
    val customField: Map<String, String> = emptyMap()
)

data class NativeAction(
    val text: String,
    val style: NativeStyle,
    val customField: Map<String, String> = emptyMap(),
    val choiceType: NativeMessageActionType = UNKNOWN,
    val legislation: CampaignType
)

data class NativeStyle(
    val fontFamily: String,
    val fontWeight: Float,
    val fontSize: Float,
    val color: String?,
    val backgroundColor: String,
)
