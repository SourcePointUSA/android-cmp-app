package com.sourcepoint.cmplibrary.data.local

import android.content.SharedPreferences
import com.fasterxml.jackson.jr.ob.impl.DeferredMap

internal interface DataStorage {
    val preference: SharedPreferences

    fun clearInternalData()

    /** store data */
    fun saveTcData(deferredMap: DeferredMap)
    fun saveAuthId(value: String)
    fun saveEuConsent(value: String)
    fun saveMetaData(value: String)
    fun saveConsentUuid(value: String)
    fun saveAppliedLegislation(value: String)

    /** fetch data */
    fun getTcData(): DeferredMap
    fun getAuthId(): String
    fun getEuConsent(): String
    fun getMetaData(): String
    fun getConsentUuid(): String
    fun getAppliedLegislation(): String

    companion object
}
