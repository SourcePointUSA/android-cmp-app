package com.sourcepoint.cmplibrary.core.layout.model

class NativeMessageDto {
    var actions: List<ActionDto> = emptyList()
    var body: TextViewConfigDto? = null
    var customFields: CustomFieldsDto? = null
    var name: String? = null
    var title: TextViewConfigDto? = null
}

class ActionDto {
    var choiceId: Int? = null
    var choiceType: Int? = null
    var customFields: CustomFieldsDto? = null
    var style: StyleDto? = null
    var text: String? = null
}

fun ActionDto.toTextViewConfigDto() = TextViewConfigDto(
    customFields = customFields,
    style = style,
    text = text
)

data class TextViewConfigDto(
    var customFields: CustomFieldsDto? = null,
    var style: StyleDto? = null,
    var text: String? = null
)

class CustomFieldsDto

class StyleDto(
    private var pBackgroundColor: String? = null,
    var color: String? = null,
    var fontFamily: String? = null,
    var fontSize: Int? = null,
    var fontWeight: String? = null
) {

    var backgroundColor: String? = null
        get() = field
            ?.let { getSixDigitHexValue(it) }

    private fun getSixDigitHexValue(colorString: String): String? {
        return if (colorString.length == 4)
            colorString.replace("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])".toRegex(), "#$1$1$2$2$3$3")
        else colorString
    }
}
