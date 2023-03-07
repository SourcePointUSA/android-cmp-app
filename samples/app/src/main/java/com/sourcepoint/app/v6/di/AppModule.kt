package com.sourcepoint.app.v6.di

import android.content.Context
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.core.create
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.* // ktlint-disable

val customVendorDataListStage = listOf("5fbe6f050d88c7d28d765d47" to "Amazon Advertising")
val customCategoriesDataStage = listOf("60657acc9c97c400122f21f3" to "Store and/or access information on a device")

val customVendorDataListProd = listOf("5e7ced57b8e05c485246cce0" to "Unruly Group LLC")
val customCategoriesDataProd = listOf(
    "608bad95d08d3112188e0e29" to "Store and/or access information on a device",
    "608bad95d08d3112188e0e2f" to "Select basic ads"

)

val appModule = module {

    single<DataProvider> {
        val gdprPmId = "488393" // stage "13111"
        val ccpaPmId = "509688" // "14967"
        val customVendorDataList = customVendorDataListProd.map { it.first }
        val customCategoriesData = customCategoriesDataProd.map { it.first }
        DataProvider.create(
            context = androidApplication(),
            spConfig = get(),
            gdprPmId = gdprPmId,
            ccpaPmId = ccpaPmId,
            customVendorList = customVendorDataList,
            customCategories = customCategoriesData,
            authId = null //get(qualifier = named("authId")))
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
        if (get(qualifier = named("prod"))) {
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                propertyId = 16893
                messLanguage = MessageLanguage.ENGLISH
                messageTimeout = 5000
                campaignsEnv = CampaignsEnv.PUBLIC
                clientSideOnly = false
                +(CampaignType.GDPR)
            }
        } else {
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                propertyId = 16893
                messLanguage = MessageLanguage.ENGLISH
                messageTimeout = 5000
                campaignsEnv = CampaignsEnv.PUBLIC
                clientSideOnly = false
                +(CampaignType.GDPR)
                +(CampaignType.CCPA to listOf(("location" to "US")))
            }

        }
    }

    single(qualifier = named("prod")) { false }

    // for tests purpose
    single<List<SpClient>> { emptyList() }
}