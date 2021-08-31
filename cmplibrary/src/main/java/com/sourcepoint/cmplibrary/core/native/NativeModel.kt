package com.sourcepoint.cmplibrary.core.native

data class NativeCategory(
    val id: String,
    val type: String,
    val name: String,
    val description: String
)

data class NativeCategories(
    val categories: List<NativeCategory> = emptyList()
)

data class NativeChoice(
    val choice_id: Long,
    val type: Int,
    val iframe_url: String? = null,
    val button_text: String? = null
)

data class NativeMessageChoices(
    val choices: List<NativeChoice> = emptyList()
)

data class NativeMessage(
    val title: NativeComponent,
    val body: NativeComponent,
    val actions: List<String>,
    val customFields: Map<String, String>
)

data class NativeComponent(
    val text: String,
    val style: NativeStyle,
    val customField: Map<String, String>
)

data class NativeActon(
    val text: String,
    val style: NativeStyle,
    val customField: Map<String, String>,
    val choiceType : Int
)

data class NativeStyle(
    val fontFamily: String,
    val fontWeight: Float,
    val fontSize: Float,
    val color: String,
    val backgroundColor: String,
)