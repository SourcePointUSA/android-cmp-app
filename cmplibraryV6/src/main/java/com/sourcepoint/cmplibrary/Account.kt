package com.sourcepoint.cmplibrary

data class Account(
    @JvmField val accountId: Int,
    @JvmField val propertyId: Int,
    @JvmField val propertyName: String,
    @JvmField val pmId: String
)
