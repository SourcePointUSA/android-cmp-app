package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat.Companion.USNAT_CONSENT_STATUS

internal interface DataStorageUSNat {

    val preference: SharedPreferences

    var usNatConsentData: String?

    fun deleteUsNatConsent()

    companion object {
        const val USNAT_CONSENT_STATUS = "sp.usnat.key.consent.status"
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

    override fun deleteUsNatConsent() {
        preference.edit()
            .apply {
                remove(USNAT_CONSENT_STATUS)
            }
            .apply()
    }
}
