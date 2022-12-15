package com.sourcepointmeta.metaapp.ui

import androidx.annotation.StringRes
import com.sourcepointmeta.metaapp.core.UIErrorCode
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import java.util.* // ktlint-disable

sealed class BaseState {
    data class StateProperty(val property: Property) : BaseState()
    data class StatePropertyList(val propertyList: List<Property>, val selectLast: Boolean = false) : BaseState()
    data class StateLogList(val propertyList: List<MetaLog>) : BaseState()
    data class StateSpItemList(val spList: SortedMap<String, List<String>>) : BaseState()
    data class StateLoading(val loading: Boolean, val propertyName: String) : BaseState()
    data class StateSharingLogs(val stringifyJson: String) : BaseState()
    data class StateJson(val json: String) : BaseState()
    data class StateError(@StringRes val errorMessage: Int) : BaseState()
    data class StateErrorValidationField(val uiCode: UIErrorCode, val message: String) : BaseState()
    data class StateVersion(val version: String) : BaseState()
    object StatePropertySaved : BaseState()
    object StateDone : BaseState()
    data class StateTvPropertySaved(val propName: String) : BaseState()
}

fun Property.hasActiveCampaign(): Boolean = statusCampaignSet.any { it.enabled }
