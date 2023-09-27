package com.sourcepoint.app.v6.core

import android.content.Context
import android.content.SharedPreferences
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import java.util.*

interface DataProvider {
    val authId: String?
    val resetAll: Boolean
    val storeStateGdpr: Boolean
    val storeStateCcpa: Boolean
    val url: String
    val gdprPmId: String
    val useGdprGroupPmIfAvailable: Boolean
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
    useGdprGroupPmIfAvailable: Boolean,
    ccpaPmId: String,
    customVendorList: List<String>,
    customCategories: List<String>,
    authId: String?
): DataProvider = DataProviderImpl(
    context = context,
    spConfig = spConfig,
    gdprPmId = gdprPmId,
    useGdprGroupPmIfAvailable = useGdprGroupPmIfAvailable,
    ccpaPmId = ccpaPmId,
    authId = authId,
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
    override val useGdprGroupPmIfAvailable: Boolean= false,
    override val resetAll: Boolean = false,
    override val storeStateGdpr: Boolean = false,
    override val storeStateCcpa: Boolean = false,
    override val authId: String? = null
) : DataProvider {

    val sharedPref: SharedPreferences by lazy {
        context.getSharedPreferences("myshared", Context.MODE_PRIVATE)
    }

    companion object {
        const val AUTH_ID_KEY = "MyAppsAuthId"
    }

    override val url: String
        get() = "https://carmelo-iriti.github.io/authid.github.io"
}