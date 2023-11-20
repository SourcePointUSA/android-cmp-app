package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.KEY_USNAT_CHILD_PM_ID
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_CONSENT_STATUS
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_SAMPLING_RESULT
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_SAMPLING_VALUE

internal interface DataStorageUSNat {

    val preference: SharedPreferences

    var usNatConsentData: String?
    var usnatChildPmId: String?

    var usNatSamplingValue: Double
    var usNatSamplingResult: Boolean?

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
        set(value) {
            value?.let {
                preference
                    .edit()
                    .putString(USNAT_CONSENT_STATUS, it)
                    .apply()
            }
        }

    override var usnatChildPmId: String?
        get() = preference.getString(KEY_USNAT_CHILD_PM_ID, null)
        set(value) {
            preference
                .edit()
                .putString(KEY_USNAT_CHILD_PM_ID, value)
                .apply()
        }

    override var usNatSamplingValue: Double
        get() = preference.getFloat(USNAT_SAMPLING_VALUE, 1.0F).toDouble()
        set(value) {
            preference
                .edit()
                .putFloat(USNAT_SAMPLING_VALUE, value.toFloat())
                .apply()
        }

    override var usNatSamplingResult: Boolean?
        get() {
            return if (preference.contains(USNAT_SAMPLING_RESULT))
                preference.getBoolean(USNAT_SAMPLING_RESULT, false)
            else null
        }
        set(value) {
            value?.let {
                preference
                    .edit()
                    .putBoolean(USNAT_SAMPLING_RESULT, it)
                    .apply()
            } ?: kotlin.run {
                preference
                    .edit()
                    .remove(USNAT_SAMPLING_RESULT)
                    .apply()
            }
        }

    override fun deleteUsNatConsent() {
        preference.edit()
            .apply {
                remove(USNAT_CONSENT_STATUS)
            }
            .apply()
    }
}
