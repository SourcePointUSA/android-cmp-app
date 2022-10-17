package com.sourcepointmeta.metaapp.tv.viewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.BaseState.StateJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class JsonViewerViewModelTv(
    private val dataSource: LocalDataSource,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun fetchJson(logId: Long) {
        viewModelScope.launch {
            val log = withContext(workerDispatcher) { dataSource.fetchLogById(logId) }.getOrNull()
            mutableLiveData.value = StateJson(log?.jsonBody ?: "{}")
        }
    }
}
