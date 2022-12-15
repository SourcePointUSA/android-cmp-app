package com.sourcepointmeta.metaapp.ui.propertylist

import androidx.lifecycle.*// ktlint-disable
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.core.fold
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.RemoteDataSource
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.BaseState.*// ktlint-disable
import com.sourcepointmeta.metaapp.util.propList
import io.github.g00fy2.versioncompare.Version
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class PropertyListViewModel(
    private val dataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun fetchPropertyList(selectLast: Boolean = false) {
        viewModelScope.launch {
            val either = withContext(workerDispatcher) { dataSource.fetchProperties() }
            either.fold(
                { /* handle the exception */ },
                {
                    mutableLiveData.value = StatePropertyList(it, selectLast)
                }
            )
        }
    }

    fun addDefaultProperties() {
        viewModelScope.launch {
            propList.forEach {
                dataSource.storeOrUpdateProperty(it)
            }
            fetchPropertyList()
        }
    }

    fun updateProperty(property: Property) {
        viewModelScope.launch {
            mutableLiveData.value = StateLoading(true, property.propertyName)
            val newProp = withContext(workerDispatcher) { dataSource.storeOrUpdateProperty(property) }.getOrNull()
            newProp?.let { mutableLiveData.value = StateProperty(it) }
            mutableLiveData.value = StateLoading(false, property.propertyName)
        }
    }

    fun deleteProperty(property: Property) {
        viewModelScope.launch(workerDispatcher) { dataSource.deleteByPropertyName(property.propertyName) }
        fetchPropertyList()
    }

    fun deleteProperty(propertyName: String) {
        viewModelScope.launch(workerDispatcher) { dataSource.deleteByPropertyName(propertyName) }
        fetchPropertyList()
    }

    fun clearDB() {
        viewModelScope.launch(workerDispatcher) {
            dataSource.deleteAll()
        }
    }

    fun fetchLatestVersion() {
        viewModelScope.launch {
            val version = withContext(workerDispatcher) { remoteDataSource.fetchLatestVersion() }
            version.fold(
                { /* handle the exception */ },
                { latestVersion ->
                    if (!Version(BuildConfig.VERSION_NAME).isEqual(latestVersion)) {
                        mutableLiveData.value = StateVersion(latestVersion)
                    }
                }
            )
        }
    }
}
