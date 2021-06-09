package com.sourcepointmeta.metaapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.chip.Chip
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.*
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.core.flatMap
import com.sourcepointmeta.metaapp.core.fold
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class AddUpdatePropertyViewModel(
    private val dataSource: LocalDataSource,
    private val validationManager: ValidationManager,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun createOrUpdateProperty(property: Property) {
        viewModelScope.launch {
            validateProperty(property)
                .map {
                    when (withContext(workerDispatcher) { dataSource.storeOrUpdateProperty(property) }) {
                        is Either.Right -> {
                            mutableLiveData.value = BaseState.StatePropertySaved
                        }
                        is Either.Left -> {
                            mutableLiveData.value = BaseState.StateError(R.string.error)
                        }
                    }
                }
                .executeOnLeft { it ->
                    (it as? MetaException)?.let {
                        mutableLiveData.value = BaseState.StateErrorValidationField(it.uiCode, it.errorMessage)
                    }
                }
        }

    }

    fun fetchProperty(propertyName: String) {
        viewModelScope.launch {
            when (val p = withContext(workerDispatcher) { dataSource.fetchPropertyByName(propertyName) }) {
                is Either.Right -> {
                    mutableLiveData.value = BaseState.StateProperty(p.r)
                }
                is Either.Left -> {
                }
            }
        }
    }

    fun deleteTargetingParam(propName: String, campaignType: CampaignType, c: Chip) {
        viewModelScope.launch(workerDispatcher) {
            val key = c.text.split(":")[0]
            dataSource.deleteTargetingParameter(propName, campaignType, key)
        }
    }

    private fun validateProperty(property: Property): Either<Property> = validationManager.run {
        validatePropertyName(property)
            .flatMap { validateAccountId(property) }
            .flatMap { validateAuthId(property) }
            .flatMap { validateCcpaPmId(property) }
            .flatMap { validateGdprPmId(property) }
            .flatMap { validateMessageLanguage(property) }
            .flatMap { validateMessageType(property) }
            .flatMap { validatePmTab(property) }
    }

}
