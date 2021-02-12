package com.sourcepoint.cmplibrary.data.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.fasterxml.jackson.jr.ob.JSON
import com.sourcepoint.cmplibrary.data.network.converter.toGDPR
import com.sourcepoint.cmplibrary.data.network.model.Gdpr
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.check

internal interface DataStorageGdpr {
    val preference: SharedPreferences
    fun saveGdpr(gdpr: Gdpr)
    fun getGdpr(): Either<Gdpr>
    companion object
}

internal fun DataStorageGdpr.Companion.create(
    context: Context
): DataStorageGdpr = DataStorageGdprImpl(context)

private class DataStorageGdprImpl(context: Context) : DataStorageGdpr {

    companion object {
        const val KEY_GDPR = "key_gdpr"
    }

    override val preference: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    override fun saveGdpr(gdpr: Gdpr) {

        val json = JSON.std.asString(gdpr)

        preference
            .edit()
            .putString(KEY_GDPR, json)
            .apply()
    }

    override fun getGdpr(): Either<Gdpr> = check {
        preference.getString(KEY_GDPR, null)
            ?.toGDPR()
            ?: fail("Gdpr")
    }

    private fun fail(param: String): Nothing = throw RuntimeException("$param not fund in local storage.")
}
