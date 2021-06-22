package com.sourcepointmeta.metaapp.ui

import androidx.lifecycle.* // ktlint-disable
import com.sourcepointmeta.metaapp.core.fold
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import com.sourcepointmeta.metaapp.ui.BaseState.* // ktlint-disable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class LogViewModel(
    private val dataSource: LocalDataSource,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    val liveDataLog: LiveData<MetaLog> get() = dataSource.logEvents.asLiveData(viewModelScope.coroutineContext)

    fun fetchLogs(propertyName: String) {
        viewModelScope.launch {
            val either = withContext(workerDispatcher) { dataSource.fetchLogsByPropertyName(propertyName) }
            either.fold(
                { /* handle the exception */ },
                {
                    mutableLiveData.value = StateLogList(it)
                }
            )
        }
    }
}
