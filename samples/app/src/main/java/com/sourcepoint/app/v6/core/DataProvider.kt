package com.sourcepoint.app.v6.core

import android.content.Context
import android.content.SharedPreferences
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import java.util.*

interface DataProvider {
    val authId : String?
    val url : String
    val onlyPm : Boolean
    val gdprPmId : String
    val spConfig : SpConfig
    companion object
}

fun DataProvider.Companion.create(
    context: Context,
    spConfig : SpConfig,
    gdprPmId : String,
    authId : String?
) : DataProvider = DataProviderImpl(context, spConfig, gdprPmId, authId)

private class DataProviderImpl(
    val context: Context,
    override val spConfig : SpConfig,
    override val gdprPmId : String,
    val pAuthId : String?
) : DataProvider {

    val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences("myshared", Context.MODE_PRIVATE)
    }

    override val onlyPm: Boolean = false

    init {
        pAuthId?.let {
            sharedPref.edit().putString(AUTH_ID_KEY, it).apply()
        }
    }

    companion object{
        const val AUTH_ID_KEY = "MyAppsAuthId"
    }

    override val url: String
        get() = "https://carmelo-iriti.github.io/authid.github.io"

    override val authId: String
        get() {
            if (!sharedPref.contains(AUTH_ID_KEY)) {
                val uniqueID = UUID.randomUUID().toString()
                sharedPref.edit().putString(AUTH_ID_KEY, uniqueID).apply()
            }
            return sharedPref.getString(AUTH_ID_KEY, "") ?: ""
        }
}