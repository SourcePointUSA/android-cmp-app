package com.sourcepoint.cmplibrary.core.nativemessage

import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType.UNKNOWN
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import java.lang.RuntimeException

internal fun JSONObject.toNativeMessageDTO(campaignType: CampaignType): MessageStructure {
    val nmMap: Map<String, Any?> = this.toTreeMap()

    return MessageStructure(
        messageComponents = nmMap.getMap("message_json")?.toMessageComponents(campaignType),
        campaignType = campaignType
    )
}

internal fun Map<String, Any?>.toMessageComponents(legislation: CampaignType): MessageComponents {
    val componentsMap = this
    return MessageComponents(
        name = componentsMap.getFieldValue<String>("name") ?: "",
        title = componentsMap.getMap("title")?.toNativeComponent(),
        body = componentsMap.getMap("body")?.toNativeComponent(),
        customFields = (this.getMap("customFields") as? Map<String, String>) ?: emptyMap(),
        actions = componentsMap.toNativeActions(legislation)
    )
}

internal fun Map<String, Any?>.toNativeActions(legislation: CampaignType): List<NativeAction> = getFieldValue<Iterable<Any?>>("actions")
    ?.filterIsInstance(Map::class.java)
    ?.map { it.toNativeAction(legislation) }
    ?: emptyList()

internal fun Map<*, *>.toNativeAction(legislation: CampaignType): NativeAction {
    val choiceType: NativeMessageActionType = (this["choiceType"] as? Int)
        ?.let { nmCode ->
            NativeMessageActionType.values().find { it.code == nmCode }
        } ?: UNKNOWN
    return NativeAction(
        text = toStringOrNull("text") ?: throw RuntimeException(),
        style = (this["style"] as? Map<String, Any?>)?.toNativeStyle() ?: throw RuntimeException(),
        choiceType = choiceType,
        legislation = legislation
    )
}

internal fun Map<String, Any?>.toNativeComponent(): NativeComponent {
    return NativeComponent(
        text = this.getFieldValue<String>("text"),
        style = this.getMap("style")?.toNativeStyle(),
        customField = (this.getMap("customFields") as? Map<String, String>) ?: emptyMap()
    )
}

internal fun Map<String, Any?>.toNativeStyle(): NativeStyle {
    val styleMap = this
    return NativeStyle(
        fontFamily = styleMap.getFieldValue<String>("fontFamily") ?: "Arial",
        fontSize = styleMap.getFieldValue<Int>("fontSize")?.toFloat() ?: 16F,
        fontWeight = styleMap.getFieldValue<String>("fontWeight")?.toFloat() ?: 400F,
        backgroundColor = styleMap.getFieldValue<String>("backgroundColor") ?: "#FFFFFF",
        color = styleMap.getFieldValue<String>("color"),
    )
}

internal fun Map<*, *>.toStringOrNull(key: String): String? {
    return if (this[key] == null || this[key].toString() == "null") null
    else this[key].toString()
}
