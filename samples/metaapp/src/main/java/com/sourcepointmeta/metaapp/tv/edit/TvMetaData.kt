package com.sourcepointmeta.metaapp.tv.edit

import android.text.InputType
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.edit.PropertyField.* //ktlint-disable

enum class PropertyField(
    val dialogTitle: String,
    val dialogDescription: String,
    val inputType: Int
) {
    PROPERTY_NAME(
        "Edit the property name field",
        "Click on the property name to edit",
        InputType.TYPE_CLASS_TEXT
    ),
    MESSAGE_LANGUAGE(
        "Edit the message language field",
        "Click on the message language to edit",
        InputType.TYPE_CLASS_TEXT
    ),
    ACCOUNT_ID(
        "Edit the accountId field",
        "Click on the accountId to edit",
        InputType.TYPE_CLASS_NUMBER
    ),
    TIMEOUT(
        "Edit the timeout field",
        "Click on the timeout to edit",
        InputType.TYPE_CLASS_NUMBER
    ),
    GDPR_PM_ID(
        "Edit the GDPR PmId field",
        "Click on the PmId to edit",
        InputType.TYPE_CLASS_NUMBER
    ),
}

fun Property.getFieldById(type: PropertyField): String {
    return when (type) {
        PROPERTY_NAME -> this.propertyName
        MESSAGE_LANGUAGE -> this.messageLanguage ?: "ENGLISH"
        ACCOUNT_ID -> this.accountId.toString()
        TIMEOUT -> this.timeout.toString()
        GDPR_PM_ID -> this.gdprPmId.toString()
    }
}
