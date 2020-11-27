package com.sourcepoint.example_app

import com.sourcepoint.example_app.core.DataProvider
import org.koin.core.module.Module
import org.koin.dsl.module

fun mockModule(uuid: String?, url : String): Module {
    return module(override = true) {
        single<DataProvider> {
            object : DataProvider {
                override val authId = uuid
                override val url = url
            }
        }
    }
}