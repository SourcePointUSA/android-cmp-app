package com.sourcepoint.app.v6.di

import android.content.Context
import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.core.create
import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.* // ktlint-disable

const val PROD = true

val appModule = module {

    single<DataProvider> {
        val gdprPmId = if (PROD) "488393" else "13111"
        val ccpaPmId = if (PROD) "14967" else "14967"
        DataProvider.create(
            context = androidApplication(),
            spConfig = get(),
            gdprPmId = gdprPmId,
            ccpaPmId = ccpaPmId,
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
        if (PROD) {
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                pmTab = PMTab.FEATURES
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
            }
        } else {
            config {
                accountId = 22
                propertyName = "mobile.multicampaign.demo"
                pmTab = PMTab.FEATURES
                messLanguage = MessageLanguage.ENGLISH
                +(CampaignType.GDPR)
                +(CampaignType.CCPA to listOf(("location" to "US")))
            }

        }
    }
}