package com.sourcepointmeta.metaapp.di

import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.data.localdatasource.create
import com.sourcepointmeta.metaapp.data.localdatasource.createDb
import com.sourcepointmeta.metaapp.ui.AddUpdatePropertyViewModel
import com.sourcepointmeta.metaapp.ui.LogViewModel
import com.sourcepointmeta.metaapp.ui.PropertyListViewModel
import com.sourcepointmeta.metaapp.ui.ValidationManager
import com.sourcepointmeta.metaapp.ui.create
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    viewModel { LogViewModel(get()) }

    viewModel { PropertyListViewModel(get()) }

    viewModel { AddUpdatePropertyViewModel(get(), get()) }

    single { ValidationManager.create() }

    single { LocalDataSource.create(get()) }

    single { createDb(androidApplication()) }

    single(qualifier = named("clear_db")) { false }
}
