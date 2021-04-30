package com.sourcepoint.cmplibrary.core.layout.model

import org.json.JSONObject

class NativeMessageDto(
    val actions: List<ActionDto> = emptyList(),
    val body: TextViewConfigDto? = null,
    val customFields: JSONObject = JSONObject(),
    val name: String? = null,
    val title: TextViewConfigDto? = null,
    val thisContent: Map<String, Any?>
)

class ActionDto(
    var choiceId: Int? = null,
    var choiceType: Int? = null,
    var customFields: JSONObject? = null,
    var style: StyleDto? = null,
    var text: String? = null
)

fun ActionDto.toTextViewConfigDto() = TextViewConfigDto(
    customFields = customFields,
    style = style,
    text = text
)

data class TextViewConfigDto(
    var customFields: JSONObject? = null,
    var style: StyleDto? = null,
    var text: String? = null
)

class StyleDto(
    pBackgroundColor: String? = null,
    val color: String? = null,
    val fontFamily: String? = null,
    val fontSize: Int? = null,
    val fontWeight: String? = null
) {

    val backgroundColor: String? = pBackgroundColor?.let { getSixDigitHexValue(it) }

    private fun getSixDigitHexValue(colorString: String): String? {
        return if (colorString.length == 4)
            colorString.replace("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(), "#$1$1$2$2$3$3")
        else colorString
    }
}
