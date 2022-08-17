package com.sourcepointmeta.metaapp.tv.ui.edit

import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.core.MetaException
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.tv.ui.PropertyTvDTO
import com.sourcepointmeta.metaapp.util.check

internal interface ValidationManagerTv {

    fun validatePropertyName(property: PropertyTvDTO): Either<PropertyTvDTO>
    fun validateAccountId(property: PropertyTvDTO): Either<PropertyTvDTO>
    fun validateMessageLanguage(property: PropertyTvDTO): Either<PropertyTvDTO>
    fun validateGdprPmId(property: PropertyTvDTO): Either<PropertyTvDTO>
    fun validateCcpaPmId(property: PropertyTvDTO): Either<PropertyTvDTO>
    fun validateAuthId(property: PropertyTvDTO): Either<PropertyTvDTO>

    companion object
}

internal fun ValidationManagerTv.Companion.create(): ValidationManagerTv = ValidationManagerTvImpl()

private class ValidationManagerTvImpl : ValidationManagerTv {

    val propNameRegex = "^[a-zA-Z.:/0-9-]*$".toRegex()

    override fun validatePropertyName(property: PropertyTvDTO): Either<PropertyTvDTO> = check {
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

    override fun validateAccountId(property: PropertyTvDTO): Either<PropertyTvDTO> = check {
        if (property.accountId == 0L)
            throw MetaException(UIErrorCode.AccountId, "AccountId not valid!")
        property
    }

    override fun validateMessageLanguage(property: PropertyTvDTO): Either<PropertyTvDTO> = check {
        MessageLanguage.values().find { it.name == property.messageLanguage.name }
            ?: throw MetaException(UIErrorCode.MessageLanguage, "MessageLanguage not valid!")
        property
    }

    override fun validateGdprPmId(property: PropertyTvDTO): Either<PropertyTvDTO> = check {
        if (property.gdprPmId == null || property.gdprPmId == "")
            throw MetaException(UIErrorCode.GdprPmId, "GdprPmId not valid!")
        property
    }

    override fun validateCcpaPmId(property: PropertyTvDTO): Either<PropertyTvDTO> = check { property }

    override fun validateAuthId(property: PropertyTvDTO): Either<PropertyTvDTO> = check { property }
}
