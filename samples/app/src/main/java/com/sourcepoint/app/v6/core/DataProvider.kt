package com.sourcepoint.app.v6.core

import android.content.Context
import android.content.SharedPreferences
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import java.util.*

interface DataProvider {
    val authId: String?
    val resetAll: Boolean
    val url: String
    val gdprPmId: String
    val ccpaPmId: String
    val spConfig: SpConfig
    val customVendorList: List<String>
    val customCategories: List<String>
    companion object
}

fun DataProvider.Companion.create(
    context: Context,
    spConfig: SpConfig,
    gdprPmId: String,
    ccpaPmId: String,
    customVendorList: List<String>,
    customCategories: List<String>,
    authId: String?
): DataProvider = DataProviderImpl(
    context = context,
    spConfig = spConfig,
    gdprPmId = gdprPmId,
    ccpaPmId = ccpaPmId,
    pAuthId = authId,
    customCategories = customCategories,
    customVendorList = customVendorList
)

private class DataProviderImpl(
    val context: Context,
    override val spConfig: SpConfig,
    override val customVendorList: List<String>,
    override val customCategories: List<String>,
    override val gdprPmId: String,
    override val ccpaPmId: String,
    override val resetAll: Boolean = false,
    val pAuthId: String?
) : DataProvider {

    val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences("myshared", Context.MODE_PRIVATE)
    }

    init {
        pAuthId?.let {
            sharedPref.edit().putString(AUTH_ID_KEY, it).apply()
        }
    }

    companion object {
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