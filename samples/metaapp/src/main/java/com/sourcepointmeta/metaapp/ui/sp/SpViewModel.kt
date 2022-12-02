package com.sourcepointmeta.metaapp.ui.sp

import android.content.SharedPreferences
import androidx.lifecycle.* // ktlint-disable
import com.sourcepointmeta.metaapp.ui.BaseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class SpViewModel(
    private val sp: SharedPreferences,
    private val workerDispatcher: CoroutineContext = Dispatchers.IO
) : ViewModel() {

    private val mutableLiveData by lazy { MutableLiveData<BaseState>() }
    val liveData: LiveData<BaseState> get() = mutableLiveData

    fun fetchItems() {
        viewModelScope.launch {
            val pairs = withContext(workerDispatcher) {
                sp.all
                    .map { Pair(it.key, listOf(it.value.toString())) }
                    .fold(hashMapOf<String, List<String>>()) {
                        acc, ele ->
                        acc[ele.first] = ele.second
                        acc
                    }
                    .toSortedMap()
            }
            mutableLiveData.value = BaseState.StateSpItemList(pairs)
        }
    }
}
