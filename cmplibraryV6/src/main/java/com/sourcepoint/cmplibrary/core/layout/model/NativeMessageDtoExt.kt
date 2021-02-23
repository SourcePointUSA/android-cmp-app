package com.sourcepoint.cmplibrary.core.layout.model

import com.sourcepoint.cmplibrary.model.getFieldValue
import com.sourcepoint.cmplibrary.model.getList
import com.sourcepoint.cmplibrary.model.getMap
import org.json.JSONObject

internal fun Map<String, Any?>.toTextViewConfigDto(): TextViewConfigDto {
    return TextViewConfigDto(
        text = getFieldValue<String>("text"),
        customFields = JSONObject(getMap("customFields")),
        style = getMap("style")?.toStyleDto()
    )
}

internal fun Map<String, Any?>.toStyleDto(): StyleDto {
    return StyleDto(
        pBackgroundColor = getFieldValue<String>("backgroundColor"),
        color = getFieldValue<String>("color"),
        fontFamily = getFieldValue<String>("fontFamily"),
        fontSize = getFieldValue<Int>("fontSize"),
        fontWeight = getFieldValue<String>("fontWeight"),
    )
}

internal fun Map<String, Any?>.toActionDto(): ActionDto {
    return ActionDto(
        choiceId = getFieldValue<Int>("choiceId"),
        choiceType = getFieldValue<Int>("choiceType"),
        customFields = JSONObject(getMap("customFields")),
        style = getMap("style")?.toStyleDto(),
        text = getFieldValue<String>("text"),
    )
}

internal fun Map<String, Any?>.toNativeMessageDto(): NativeMessageDto {
    val actions = getList("actions")
    val body = getMap("body")
    val customFields = getMap("customFields")
    val name = getFieldValue<String>("name")
    val title = getMap("title")

    return NativeMessageDto(
        actions = actions?.map { it.toActionDto() } ?: emptyList(),
        customFields = JSONObject(customFields),
        thisContent = this,
        name = name,
        title = title?.toTextViewConfigDto(),
        body = body?.toTextViewConfigDto()
    )
}
