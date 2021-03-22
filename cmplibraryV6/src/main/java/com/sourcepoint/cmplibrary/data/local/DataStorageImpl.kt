package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE

/**
 * Factory method to create an instance of a [DataStorage] using its implementation
 * @param context is the client application context
 * @return an instance of the [DataStorageImpl] implementation
 */
internal fun DataStorage.Companion.create(
    context: Context,
    dsGdpr: DataStorageGdpr,
    dsCcpa: DataStorageCcpa
): DataStorage = DataStorageImpl(context, dsGdpr, dsCcpa)

private class DataStorageImpl(
    context: Context,
    dsGdpr: DataStorageGdpr,
    dsCcpa: DataStorageCcpa
) : DataStorage,
    DataStorageGdpr by dsGdpr,
    DataStorageCcpa by dsCcpa {

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveLocalState(localState: String) {
        preference
            .edit()
            .putString(LOCAL_STATE, localState)
            .apply()
    }

    override fun getLocalState(): String? {
        return preference.getString(LOCAL_STATE, null)
    }

    companion object
}
