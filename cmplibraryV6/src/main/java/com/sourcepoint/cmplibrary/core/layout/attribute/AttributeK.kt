package com.sourcepoint.cmplibrary.core.layout.attribute

import java.util.*

data class AttributeK(
    val text: String,
    val style: StyleK,
    val customFields: HashMap<String, String> = HashMap()
)