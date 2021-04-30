package com.sourcepoint.app.v6.di

import android.content.Context
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.core.create
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.*

val appModule = module {

    single<DataProvider> {
        DataProvider.create(androidApplication(), get(), "13111",null)//get(qualifier = named("authId")))
    }

    single<String?>(qualifier = named("authId")) {
        val sp = androidContext().getSharedPreferences("appPref", Context.MODE_PRIVATE)
        when(sp.contains("authId")){
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
            +(CampaignType.CCPA to listOf(("location" to "US")))
            +(CampaignType.GDPR) //to listOf(("location" to "EU")))
        }
    }
}