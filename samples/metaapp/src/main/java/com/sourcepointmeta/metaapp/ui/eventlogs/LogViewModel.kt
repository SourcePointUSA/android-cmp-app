package com.sourcepointmeta.metaapp.ui.eventlogs

import androidx.lifecycle.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.core.fold
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.MetaLog
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.BaseState.* // ktlint-disable
import com.sourcepointmeta.metaapp.ui.component.LogItem
import com.sourcepointmeta.metaapp.ui.component.toLogItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class LogViewModel(
    private val dataSource: LocalDataSource,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    val liveDataLog: LiveData<LogItem>
        get() = dataSource.logEvents
            .map { it.toLogItem() }
            .asLiveData(workerDispatcher)

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

    fun resetDataByProperty(propertyName: String) {
        viewModelScope.launch(workerDispatcher) {
            dataSource.deleteLogsByPropertyName(propertyName)
        }
    }

    fun fetchLogs(propertyName: String, ids: Collection<Long>) {
        viewModelScope.launch {
            val logs: Either<List<MetaLog>> = withContext(workerDispatcher) {
                if (ids.isEmpty()) dataSource.fetchLogsByPropertyName(propertyName)
                else dataSource.fetchLogByIds(ids)
            }
            logs.fold(
                { /* handle the exception */ },
                { list -> mutableLiveData.value = StateSharingLogs(list.toStringifyJson()) }
            )
        }
    }

    fun getConfig(propertyName: String): Either<SpConfig> {
        return dataSource.getSPConfig(propertyName)
    }
}
