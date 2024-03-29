package com.sourcepointmeta.metaapp.tv.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.* //ktlint-disable
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.tv.updateDTO
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.ValidationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class AddUpdatePropertyViewModelTv(
    val dataSource: LocalDataSource,
    private val validationManager: ValidationManager,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun createOrUpdateProperty(propertyTv: Property, fieldType: PropertyField, newField: String?) {
        viewModelScope.launch {
            validateProperty(propertyTv)
                .map {
                    if (fieldType == PropertyField.PROPERTY_NAME) {
                        dataSource.deleteByPropertyName(propertyTv.propertyName)
                    }
                    val np = propertyTv.updateDTO(fieldType, newField)
                    when (withContext(workerDispatcher) { dataSource.storeOrUpdateProperty(np) }) {
                        is Either.Right -> {
                            mutableLiveData.value = BaseState.StateTvPropertySaved(np.propertyName)
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

    fun fetchPropertySync(propertyName: String): Property {
        // make it async
        return dataSource.fetchPropertyByNameSync(propertyName)
    }

    fun fetchPropertyOrDefault(propertyName: String, default: Property) {
        viewModelScope.launch {
            dataSource.fetchPropertyByName(propertyName)
                .map { mutableLiveData.value = BaseState.StateProperty(it) }
                .executeOnLeft {
                    dataSource.storeOrUpdateProperty(default)
                    mutableLiveData.value = BaseState.StateProperty(default)
                }
        }
    }

    fun fetchProperty(propertyName: String) {
        viewModelScope.launch {
            dataSource.fetchPropertyByName(propertyName)
                .map { mutableLiveData.value = BaseState.StateProperty(it) }
                .executeOnLeft { mutableLiveData.value = BaseState.StateError(R.string.error) }
        }
    }

    fun deletePropertySync(propertyName: String) {
        // make it async
        return runBlocking { dataSource.deleteByPropertyName(propertyName) }
    }

    fun duplicatePropertySync(propertyName: String) {
        // make it async
        runBlocking {
            dataSource.fetchPropertyByName(propertyName)
                .map {
                    dataSource.storeOrUpdateProperty(it.copy(propertyName = it.propertyName + ".copy"))
                }
        }
    }

    private fun validateProperty(property: Property): Either<Property> = validationManager.run {
        validatePropertyName(property)
            .flatMap { validateAccountId(property) }
            .flatMap { validateCcpaPmId(property) }
            .flatMap { validateGdprPmId(property) }
            .flatMap { validateAuthId(property) }
            .flatMap { validateMessageLanguage(property) }
    }
}
