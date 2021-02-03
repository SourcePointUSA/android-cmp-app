package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences
import com.fasterxml.jackson.jr.ob.impl.DeferredMap

internal interface DataStorage {
    val preference: SharedPreferences
    fun saveTcData(deferredMap: DeferredMap)
    fun getTcData(): DeferredMap
    fun clearInternalData()
    fun saveAuthId(value: String)
    fun saveEuConsent(value: String)
    fun saveMetaData(value: String)
    fun saveConsentUuid(value: String)
    fun getAuthId(): String
    fun getEuConsent(): String
    fun getMetaData(): String
    fun getConsentUuid(): String

    companion object
}
