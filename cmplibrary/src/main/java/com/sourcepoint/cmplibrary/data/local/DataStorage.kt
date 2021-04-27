package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {

    companion object {
        const val LOCAL_STATE = "key_local_state"
        const val PROPERTY_PRIORITY_DATA = "key_property_priority_data"
        const val PROPERTY_ID = "key_property_id"
    }

    override val preference: SharedPreferences
    fun savePropertyId(value: Int)
    fun savePropertyPriorityData(value: String)
    fun saveLocalState(value: String)
    fun getLocalState(): String?
    fun getPropertyId(): Int
    fun getPropertyPriorityData(): String?
}
