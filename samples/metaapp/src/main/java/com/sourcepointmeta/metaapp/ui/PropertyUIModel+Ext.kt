package com.sourcepointmeta.metaapp.ui

import androidx.annotation.StringRes
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.data.localdatasource.Property

sealed class BaseState {
    data class StateProperty(val property: Property) : BaseState()
    data class StateSuccess(val propertyList: List<Property>) : BaseState()
    data class StateError(@StringRes val errorMessage: Int) : BaseState()
    data class StateErrorValidationField(val uiCode : UIErrorCode, val message : String) : BaseState()
    object StatePropertySaved : BaseState()
}
