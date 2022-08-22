package com.sourcepointmeta.metaapp.tv.ui

import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.ui.edit.PropertyField
import com.sourcepointmeta.metaapp.tv.ui.edit.PropertyField.* //ktlint-disable

fun Property.updateDTO(fieldType: PropertyField, newField: String?): Property {
    newField ?: return this
    return when (fieldType) {
        PROPERTY_NAME -> this.copy(propertyName = newField)
        MESSAGE_LANGUAGE -> this.copy(messageLanguage = newField)
        ACCOUNT_ID -> this.copy(accountId = newField.toLongOrNull() ?: 1)
        TIMEOUT -> this.copy(timeout = newField.toLongOrNull() ?: 3000L)
    }
}
