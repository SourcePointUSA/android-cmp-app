package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

internal interface DataStorage {
    val preference: SharedPreferences
    companion object
}

internal fun DataStorage.Companion.create(context: Context): DataStorage = DataStorageImpl(context)

private class DataStorageImpl(context: Context) : DataStorage {

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }
}
