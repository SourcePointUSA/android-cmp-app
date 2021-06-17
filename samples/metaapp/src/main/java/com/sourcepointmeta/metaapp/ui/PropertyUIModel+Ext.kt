package com.sourcepointmeta.metaapp.ui

import androidx.annotation.StringRes
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import com.sourcepointmeta.metaapp.data.localdatasource.Property

sealed class BaseState {
    data class StateProperty(val property: Property) : BaseState()
    data class StatePropertyList(val propertyList: List<Property>) : BaseState()
    data class StateLogList(val propertyList: List<MetaLog>) : BaseState()
    data class StateLoading(val loading: Boolean, val propertyName: String) : BaseState()
    data class StateError(@StringRes val errorMessage: Int) : BaseState()
    data class StateErrorValidationField(val uiCode: UIErrorCode, val message: String) : BaseState()
    object StatePropertySaved : BaseState()
}

fun Property.hasActiveCampaign(): Boolean = statusCampaignSet.any { it.enabled }
