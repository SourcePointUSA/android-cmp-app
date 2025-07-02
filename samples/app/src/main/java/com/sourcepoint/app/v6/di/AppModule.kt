package com.sourcepoint.app.v6.di

import android.content.Context
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.core.create
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.util.CampaignType
import com.sourcepoint.cmplibrary.model.CampaignsEnv
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.* // ktlint-disable

val customVendorDataListStage = listOf("5fbe6f050d88c7d28d765d47" to "Amazon Advertising")
val customCategoriesDataStage = listOf("60657acc9c97c400122f21f3" to "Store and/or access information on a device")

val customVendorDataListProd = listOf("5ff4d000a228633ac048be41" to "Game Accounts")
val customCategoriesDataProd = listOf(
    "608bad95d08d3112188e0e36" to "Create profiles for personalised advertising",
    "608bad95d08d3112188e0e2f" to "Use limited data to select advertising",
    "608bad95d08d3112188e0e3d" to "Use profiles to select personalised advertising"
)

val appModule = module {
    single<DataProvider> {
        val gdprPmId = "488393" // stage "13111"
        val ccpaPmId = "509688" // "14967"
        val usnatPmId = "988851"
        val globalcmpPmId = "1323762"
        val preferencesPmId = "1319982"
        val customVendorDataList = customVendorDataListProd.map { it.first }
        val customCategoriesData = customCategoriesDataProd.map { it.first }
        DataProvider.create(
            context = androidApplication(),
            spConfig = get(),
            gdprPmId = gdprPmId,
            ccpaPmId = ccpaPmId,
            usnatPmId = usnatPmId,
            globalcmpPmId = globalcmpPmId,
            preferencesPmId = preferencesPmId,
            customVendorList = customVendorDataList,
            customCategories = customCategoriesData,
            useGdprGroupPmIfAvailable = false,
            authId = null, //get(qualifier = named("authId")))
            diagnostic = emptyList(),
        )
    }

    single<String?>(qualifier = named("authId")) {
        val sp = androidContext().getSharedPreferences("appPref", Context.MODE_PRIVATE)
        when (sp.contains("authId")) {
            true -> sp.getString("authId", "")!!
            false -> {
                "${Date().time}".also { sp.edit().putString("authId", it).apply() }
            }
        }
    }

    single<SpConfig> {
        config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            propertyId = 16893
            messLanguage = MessageLanguage.ENGLISH
            campaignsEnv = CampaignsEnv.PUBLIC
            +(CampaignType.GDPR)
            +(CampaignType.CCPA)
            +(CampaignType.USNAT)
            +(CampaignType.PREFERENCES)
            +(CampaignType.GLOBALCMP)
        }
    }

    single(qualifier = named("prod")) { false }

    // for tests purpose
    single<List<SpClient>> { emptyList() }

    single<ConnectionManager> { object : ConnectionManager {
        override val isConnected = true
    } }

    single<Boolean>(named("dismissMessageOnBackPress")) { true }
}
