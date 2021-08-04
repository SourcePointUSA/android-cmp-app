package com.sourcepointmeta.metaapp.di

import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.RemoteDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.create
import com.sourcepointmeta.metaapp.data.localdatasource.createDb
import com.sourcepointmeta.metaapp.ui.ValidationManager
import com.sourcepointmeta.metaapp.ui.create
import com.sourcepointmeta.metaapp.ui.eventlogs.LogViewModel
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyViewModel
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModel { LogViewModel(get()) }

    viewModel { PropertyListViewModel(get(), get()) }

    viewModel { AddUpdatePropertyViewModel(get(), get()) }

    viewModel { JsonViewerViewModel(get()) }

    single { ValidationManager.create() }

    single { LocalDataSource.create(get()) }

    single { RemoteDataSource.create(BuildConfig.URL_PROPERTY_FILE) }

    single { createDb(androidApplication()) }

    single(qualifier = named("clear_db")) { false }

    single(qualifier = named("ui_test_running")) { false }
}
