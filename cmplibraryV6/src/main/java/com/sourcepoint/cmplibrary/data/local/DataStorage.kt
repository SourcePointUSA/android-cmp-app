package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

internal interface DataStorage {
    val spEditor : SharedPreferences.Editor
    companion object
}

internal fun DataStorage.Companion.create(context: Context) : DataStorage = DataStorageImpl(context)

private class DataStorageImpl(context: Context) : DataStorage{

    private val sp by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override val spEditor: SharedPreferences.Editor by lazy { sp.edit() }
}