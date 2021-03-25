package com.sourcepoint.app.v6.di

import com.sourcepoint.app.v6.core.DataProvider
import com.sourcepoint.app.v6.core.create
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single<DataProvider> {
        DataProvider.create(androidApplication())
    }
}