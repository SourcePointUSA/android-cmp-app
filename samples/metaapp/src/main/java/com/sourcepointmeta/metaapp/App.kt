package com.sourcepointmeta.metaapp

import android.app.Application
import com.sourcepointmeta.metaapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Use Koin Android Logger
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            // declare Android context
            androidContext(this@App)
            // declare modules to use
            modules(appModule)
        }
    }
}
