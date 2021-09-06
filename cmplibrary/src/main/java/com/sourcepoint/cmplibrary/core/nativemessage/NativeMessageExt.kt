package com.sourcepoint.cmplibrary.core.nativemessage

import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType
import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getMap
import com.sourcepoint.cmplibrary.model.toTreeMap
import org.json.JSONObject
import java.lang.RuntimeException

fun JSONObject.toNativeMessageDTO(): MessageStructure {
    val nmMap: Map<String, Any?> = this.toTreeMap()

    val messageChoices = nmMap.getFieldValue<Iterable<Any?>>("message_choice")
        ?.filterIsInstance(Map::class.java)
        ?.map {
            NativeChoice(
                button_text = it.toStringOrNull("button_text"),
                type = it["type"] as? Int,
                choice_id = it["choice_id"] as? Int
            )
        }
        ?: failParam("message_choice in NativeMessage")

    return MessageStructure(
        messageComponents = nmMap.getMap("message_json")?.toMessageComponents(),
        messageChoices = messageChoices
    )
}

fun Map<String, Any?>.toMessageComponents(): MessageComponents {
    val componentsMap = this
    return MessageComponents(
        name = componentsMap.getFieldValue<String>("name") ?: "",
        title = componentsMap.getMap("title")?.toNativeComponent(),
        body = componentsMap.getMap("body")?.toNativeComponent(),
        customFields = (this.getMap("customFields") as? Map<String, String>) ?: emptyMap(),
        actions = componentsMap.toNativeActions()
    )
}

fun Map<String, Any?>.toNativeActions(): List<NativeAction> = getFieldValue<Iterable<Any?>>("actions")
    ?.filterIsInstance(Map::class.java)
    ?.map { it.toNativeAction() }
    ?: emptyList()

fun Map<*, *>.toNativeAction(): NativeAction {
    return NativeAction(
        text = toStringOrNull("text") ?: throw RuntimeException(),
        style = (this["style"] as? Map<String, Any?>)?.toNativeStyle() ?: throw RuntimeException(),
        choiceType = (this["choiceType"] as? Int)
            ?.let { nmCode ->
                NativeMessageActionType.values().find { it.code == nmCode }
            }?: throw RuntimeException()
    )
}

fun Map<String, Any?>.toNativeComponent(): NativeComponent {
    return NativeComponent(
        text = this.getFieldValue<String>("text"),
        style = this.getMap("style")?.toNativeStyle(),
        customField = (this.getMap("customFields") as? Map<String, String>) ?: emptyMap()
    )
}

fun Map<String, Any?>.toNativeStyle(): NativeStyle {
    val styleMap = this
    return NativeStyle(
        fontFamily = styleMap.getFieldValue<String>("fontFamily"),
        fontSize = styleMap.getFieldValue<Int>("fontSize")?.toFloat(),
        fontWeight = styleMap.getFieldValue<String>("fontWeight")?.toFloat(),
        backgroundColor = styleMap.getFieldValue<String>("backgroundColor"),
        color = styleMap.getFieldValue<String>("color"),
    )
}

fun Map<*, *>.toStringOrNull(key: String): String? {
    return if (this[key] == null || this[key].toString() == "null") null
    else this[key].toString()
}