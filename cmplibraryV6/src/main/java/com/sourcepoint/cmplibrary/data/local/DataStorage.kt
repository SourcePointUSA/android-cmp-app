package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {

    companion object {
        const val LOCAL_STATE = "key_local_state"
    }

    override val preference: SharedPreferences
    fun saveLocalState(localState: String)
    fun getLocalState(): String?
}
