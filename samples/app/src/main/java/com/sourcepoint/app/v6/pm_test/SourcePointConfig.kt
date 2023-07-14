package com.sourcepoint.app.v6.pm_test

sealed class SourcePointConfig {

    data class Loaded(
        val accountId: Int,
        val propertyName: String,
        val propertyId: Int,
        val gdprId: String,
        val ccpaId: String
    ) : SourcePointConfig()

    object Empty : SourcePointConfig()


    companion object {

        fun hardcoded() = Loaded(
            accountId = 1789,
            propertyName = "badoo.android",
            propertyId = 27195,
            gdprId = "759246",
            ccpaId = "759254"
        )
    }
}
