package com.sourcepointmeta.metaapp.ui

import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.core.MetaException
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment
import com.sourcepointmeta.metaapp.util.check

internal interface ValidationManager {

    fun validatePropertyName(property: Property): Either<Property>
    fun validateAccountId(property: Property): Either<Property>
    fun validateMessageLanguage(property: Property): Either<Property>
    fun validateGdprPmId(property: Property): Either<Property>
    fun validateCcpaPmId(property: Property): Either<Property>
    fun validateAuthId(property: Property): Either<Property>
    fun validatePmTab(property: Property): Either<Property>

    companion object
}

internal fun ValidationManager.Companion.create(): ValidationManager = ValidationManagerImpl()

private class ValidationManagerImpl : ValidationManager {

    val propNameRegex = "^[a-zA-Z.:/0-9-]*$".toRegex()

    override fun validatePropertyName(property: Property): Either<Property> = check {
        if (!property.propertyName.matches(propNameRegex))
            throw MetaException(
                UIErrorCode.PropertyName,
                "PropertyName can only include letters, numbers, '.', ':', '-' and '/'."
            )
        if (property.propertyName.length < 3)
            throw MetaException(
                UIErrorCode.PropertyName,
                "PropertyName cannot be less that 3 chars!"
            )
        property
    }

    override fun validateAccountId(property: Property): Either<Property> = check {
        if (property.accountId == 0L)
            throw MetaException(UIErrorCode.AccountId, "AccountId not valid!")
        property
    }

    override fun validateMessageLanguage(property: Property): Either<Property> = check {
        MessageLanguage.values().find { it.name == property.messageLanguage }
            ?: throw MetaException(UIErrorCode.MessageLanguage, "MessageLanguage not valid!")
        property
    }

    override fun validateGdprPmId(property: Property): Either<Property> = check {
        if (property.gdprPmId == null || property.gdprPmId == 0L)
            throw MetaException(UIErrorCode.GdprPmId, "GdprPmId not valid!")
        property
    }

    override fun validateCcpaPmId(property: Property): Either<Property> = check { property }

    override fun validateAuthId(property: Property): Either<Property> = check { property }

    override fun validatePmTab(property: Property): Either<Property> = check {
        PMTab.values().find { it.name == property.pmTab }
            ?: throw MetaException(UIErrorCode.PmTab, "Privacy Manager Tab not valid!")
        property
    }
}
