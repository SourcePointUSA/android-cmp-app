package com.example.myapplication

class Accounts {
    companion object {
        @JvmField
        val webAccount = Account(
            accountId = 22,
            propertyId = 7639,
            propertyName = "tcfv2.mobile.webview",
            pmId = "122058"
        )

        @JvmField
        val nativeAccount = Account(
            accountId = 22,
            propertyId = 7094,
            propertyName = "tcfv2.mobile.demo",
            pmId = "179657"
        )
    }

}

data class Account(
    @JvmField val accountId: Int,
    @JvmField val propertyId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)