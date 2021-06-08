package com.sourcepointmeta.metaapp.ui

import androidx.lifecycle.* // ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.core.fold
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class PropertyListViewModel(
    private val dataSource: LocalDataSource,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun fetchPropertyList() {
        viewModelScope.launch {
            val either = withContext(workerDispatcher) { dataSource.fetchProperties() }
            either.fold(
                { /* handle the exception */ },
                {
                    mutableLiveData.value = BaseState.StateSuccess(it)
                    List(15) {
                        Property(
                            statusCampaignSet = setOf(StatusCampaign(campaignType = CampaignType.GDPR, enabled = true, propertyName = "abc")),
                            propertyName = "abd  $it",
                            messageType = "App",
                            pmId = "",
                            accountId = 22 + it.toLong(),
                            is_staging = true,
                            targetingParameters = emptyList(),
                            messageLanguage = null,
                            authId = null,
                            propertyId = null,
                            pmTab = null
                        )
                    }.let { mutableLiveData.value = BaseState.StateSuccess(it) }
                }
            )
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch(workerDispatcher) { dataSource.storeOrUpdateProperty(property) }
    }

    fun deleteProperty(property: Property) {
        viewModelScope.launch(workerDispatcher) { dataSource.deleteByPropertyName(property.propertyName) }
        fetchPropertyList()
    }

    fun deleteProperty(propertyName: String) {
        viewModelScope.launch(workerDispatcher) { dataSource.deleteByPropertyName(propertyName) }
        fetchPropertyList()
    }
}
