package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.consent.ConsentManagerUtils.Companion.DEFAULT_SAMPLE_RATE
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.KEY_USNAT_CHILD_PM_ID
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_CONSENT_STATUS
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_SAMPLING_RESULT
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_SAMPLING_VALUE

internal interface DataStorageUSNat {

    val preference: SharedPreferences

    var usNatConsentData: String?
    var usnatChildPmId: String?

    var usnatSampleRate: Double
    var usnatSampled: Boolean?

    fun deleteUsNatConsent()

    companion object {
        const val USNAT_CONSENT_STATUS = "sp.usnat.key.consent.status"
        const val USNAT_SAMPLING_VALUE = "sp.usnat.key.sampling"
        const val USNAT_SAMPLING_RESULT = "sp.usnat.key.sampling.result"
        const val KEY_USNAT_CHILD_PM_ID = "sp.usnat.key.childPmId"
    }
}

internal fun DataStorageUSNat.Companion.create(
    context: Context
): DataStorageUSNat = DataStorageUSNatImpl(context)

private class DataStorageUSNatImpl(context: Context) : DataStorageUSNat {
    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override var usNatConsentData: String?
        get() = preference.getString(USNAT_CONSENT_STATUS, null)
        set(value) = preference.putString(USNAT_CONSENT_STATUS, value)

    override var usnatChildPmId: String?
        get() = preference.getString(KEY_USNAT_CHILD_PM_ID, null)
        set(value) = preference.putString(KEY_USNAT_CHILD_PM_ID, value)

    override var usnatSampleRate: Double
        get() = preference.getFloat(USNAT_SAMPLING_VALUE, DEFAULT_SAMPLE_RATE.toFloat()).toDouble()
        set(value) = preference.putFloat(USNAT_SAMPLING_VALUE, value.toFloat())

    override var usnatSampled: Boolean?
        get() = preference.getBoolean(USNAT_SAMPLING_RESULT)
        set(value) = preference.putBoolean(USNAT_SAMPLING_RESULT, value)

    override fun deleteUsNatConsent() = preference.edit()
        .apply {
            remove(USNAT_CONSENT_STATUS)
            remove(KEY_USNAT_CHILD_PM_ID)
            remove(USNAT_SAMPLING_RESULT)
            remove(USNAT_SAMPLING_VALUE)
            preference
                .all
                .filter { entry -> entry.key.startsWith(DataStorageCcpa.KEY_IABGPP_PREFIX) }
                .keys
                .forEach { gppKey -> remove(gppKey) }
        }
        .apply()
}

fun SharedPreferences.getBoolean(key: String): Boolean? {
    return if (contains(key)) { getBoolean(key, false) } else null
}

fun SharedPreferences.putFloat(key: String, value: Float?) {
    if (value != null) {
        edit().putFloat(key, value).apply()
    } else {
        edit().remove(key).apply()
    }
}

fun SharedPreferences.putBoolean(key: String, value: Boolean?) {
    if (value != null) {
        edit().putBoolean(key, value).apply()
    } else {
        edit().remove(key).apply()
    }
}

fun SharedPreferences.putString(key: String, value: String?) = edit().putString(key, value).apply()
