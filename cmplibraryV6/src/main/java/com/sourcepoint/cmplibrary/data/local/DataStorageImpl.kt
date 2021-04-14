package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.PROPERTY_ID
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.PROPERTY_PRIORITY_DATA

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

    override fun saveLocalState(value: String) {
        preference
            .edit()
            .putString(LOCAL_STATE, value)
            .apply()
    }

    override fun getLocalState(): String? {
        return preference.getString(LOCAL_STATE, null)
    }

    override fun savePropertyId(value: Int) {
        preference
            .edit()
            .putInt(PROPERTY_ID, value)
            .apply()
    }

    override fun savePropertyPriorityData(value: String) {
        preference
            .edit()
            .putString(PROPERTY_PRIORITY_DATA, value)
            .apply()
    }

    override fun getPropertyId(): Int {
        return preference.getInt(PROPERTY_ID, -1)
    }

    override fun getPropertyPriorityData(): String? {
        return preference.getString(PROPERTY_PRIORITY_DATA, null)
    }

    override fun clearAll() {
        preference.edit().clear().apply()
    }

    companion object
}
