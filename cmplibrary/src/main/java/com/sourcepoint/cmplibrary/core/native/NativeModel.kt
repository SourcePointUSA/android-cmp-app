package com.sourcepoint.cmplibrary.core.native

import com.sourcepoint.cmplibrary.model.exposed.ActionType

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

data class NativeAction(
    val text: String,
    val style: NativeStyle,
    val customField: Map<String, String>,
    val choiceType : ActionType
)

data class NativeStyle(
    val fontFamily: String,
    val fontWeight: Float,
    val fontSize: Float,
    val color: String,
    val backgroundColor: String,
)