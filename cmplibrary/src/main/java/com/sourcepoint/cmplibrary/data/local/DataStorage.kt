package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences

internal interface DataStorage : DataStorageGdpr, DataStorageCcpa {

    companion object {
        const val LOCAL_STATE = "sp.key.local.state"
        const val PROPERTY_PRIORITY_DATA = "sp.key.property.priority.data"
        const val PROPERTY_ID = "sp.key.property.id"
    }

    override val preference: SharedPreferences
    fun savePropertyId(value: Int)
    fun savePropertyPriorityData(value: String)
    fun saveLocalState(value: String)
    fun getLocalState(): String?
    fun getPropertyId(): Int
    fun getPropertyPriorityData(): String?
}
