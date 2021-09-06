package com.sourcepoint.cmplibrary.core.nativemessage

import com.sourcepoint.cmplibrary.model.exposed.ActionType
import com.sourcepoint.cmplibrary.model.exposed.NativeMessageActionType

data class MessageStructure(
    val messageComponents : MessageComponents?,
    val messageChoices: List<NativeChoice> = emptyList()
)

data class MessageComponents(
    val name : String,
    val title : NativeComponent?,
    val body : NativeComponent?,
    val actions : List<NativeAction> = emptyList(),
    val customFields : Map<String,String> = emptyMap()
)

data class NativeChoice(
    val choice_id: Int?,
    val type: Int?,
    val iframe_url: String? = null,
    val button_text: String? = null
)

data class NativeCategory(
    val id: String,
    val type: String,
    val name: String,
    val description: String
)

data class NativeCategories(
    val categories: List<NativeCategory> = emptyList()
)

data class NativeMessage(
    val title: NativeComponent,
    val body: NativeComponent,
    val actions: List<String> = emptyList(),
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
    val choiceType: NativeMessageActionType
)

data class NativeStyle(
    val fontFamily: String?,
    val fontWeight: Float?,
    val fontSize: Float?,
    val color: String?,
    val backgroundColor: String?,
)