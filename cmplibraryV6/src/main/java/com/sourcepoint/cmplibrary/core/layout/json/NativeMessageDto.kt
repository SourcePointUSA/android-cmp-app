package com.sourcepoint.cmplibrary.core.layout.json

class NativeMessageDto {
    var actions: List<Action>? = null
    var body: Body? = null
    var customFields: CustomFields? = null
    var name: String? = null
    var title: Title? = null
}

class Action {
    var choiceId: Int? = null
    var choiceType: Int? = null
    var customFields: CustomFields? = null
    var style: Style? = null
    var text: String? = null
}

class Body {
    var customFields: CustomFields? = null
    var style: Style? = null
    var text: String? = null
}

class CustomFields

class Style {
    var backgroundColor: String? = null
    var color: String? = null
    var fontFamily: String? = null
    var fontSize: Int? = null
    var fontWeight: String? = null
}

class Title {
    var customFields: CustomFields? = null
    var style: Style? = null
    var text: String? = null
}
