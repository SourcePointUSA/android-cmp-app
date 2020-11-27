package com.sourcepoint.example_app.di

import com.sourcepoint.example_app.core.DataProvider
import com.sourcepoint.example_app.core.create
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    single<DataProvider> {
        DataProvider.create(androidApplication())
    }
}