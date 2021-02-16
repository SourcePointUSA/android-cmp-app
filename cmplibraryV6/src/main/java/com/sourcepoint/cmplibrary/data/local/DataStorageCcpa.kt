package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fasterxml.jackson.jr.ob.JSON
import com.sourcepoint.cmplibrary.data.network.converter.toCCPA
import com.sourcepoint.cmplibrary.data.network.model.Ccpa
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check

internal interface DataStorageCcpa {
    val preference: SharedPreferences
    fun saveCcpa(ccpa: Ccpa)
    fun getCcpa(): Either<Ccpa>
    var ccpaApplies: Boolean
    companion object
}

internal fun DataStorageCcpa.Companion.create(
    context: Context
): DataStorageCcpa = DataStorageCcpaImpl(context)

private class DataStorageCcpaImpl(context: Context) : DataStorageCcpa {

    companion object {
        const val KEY_CCPA = "key_ccpa"
        const val KEY_CCPA_APPLIES = "key_ccpa_applies"
    }

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveCcpa(ccpa: Ccpa) {

        val json = JSON.std.asString(ccpa)

        preference
            .edit()
            .putString(KEY_CCPA, json)
            .apply()
    }

    override fun getCcpa(): Either<Ccpa> = check {
        preference.getString(KEY_CCPA, null)
            ?.toCCPA()
            ?: fail("Ccpa")
    }

    override var ccpaApplies: Boolean
        get() = preference.getBoolean(KEY_CCPA_APPLIES, false)
        set(value) {
            preference
                .edit()
                .putBoolean(KEY_CCPA_APPLIES, value)
                .apply()
        }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}
