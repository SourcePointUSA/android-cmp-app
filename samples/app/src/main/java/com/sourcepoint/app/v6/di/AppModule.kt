package com.sourcepoint.app.v6.di

import android.content.Context
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.core.create
import com.sourcepoint.cmplibrary.creation.config
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

val customVendorDataListProd = listOf("5ff4d000a228633ac048be41" to "-")
val customCategoriesDataProd = listOf(
    "608bad95d08d3112188e0e29" to "-",
    "608bad95d08d3112188e0e2f" to "-"

)

val appModule = module {

    single<DataProvider> {
        val gdprPmId = if (get(qualifier = named("prod"))) "488393" else "13111"
        val ccpaPmId = if (get(qualifier = named("prod"))) "14967" else "14967"
        val customVendorDataList = if (get(qualifier = named("prod"))) {
            customVendorDataListProd.map { it.first }
        } else {
            customVendorDataListStage.map { it.first }
        }
        val customCategoriesData = if (get(qualifier = named("prod"))) {
            customCategoriesDataProd.map { it.first }
        } else {
            customCategoriesDataStage.map { it.first }
        }
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
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
            }
        } else {
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
//                +(CampaignType.CCPA to listOf(("location" to "US")))
            }

        }
    }

    single(qualifier = named("prod")) { false }
}