package com.sourcepointmeta.metaapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.chip.Chip
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class AddUpdatePropertyViewModel(
    private val dataSource: LocalDataSource,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun createProperty(property: Property) {
        viewModelScope.launch {
            when (withContext(workerDispatcher) { dataSource.storeOrUpdateProperty(property) }) {
                is Either.Right -> {
                    mutableLiveData.value = BaseState.StatePropertySaved
                }
                is Either.Left -> {
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

    fun deleteTargetingParam(propName : String, campaignType: CampaignType, c : Chip) {
        viewModelScope.launch(workerDispatcher) {
            val key = c.text.split(":")[0]
            dataSource.deleteTargetingParameter(propName, campaignType, key)
        }
    }
}