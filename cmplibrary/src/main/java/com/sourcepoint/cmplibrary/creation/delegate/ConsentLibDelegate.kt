package com.sourcepoint.cmplibrary.creation.delegate

import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.SpCmpBuilder

fun spConsentLibLazy(dsl: SpCmpBuilder.() -> Unit): Lazy<SpConsentLib> = ConsentLibDelegate(dsl)

class ConsentLibDelegate(
    private val cmpDsl: SpCmpBuilder.() -> Unit
) : Lazy<SpConsentLib> {

    private var libSp: SpConsentLib? = null

    override val value: SpConsentLib
        get() = libSp ?: run {
            val builder = SpCmpBuilder().apply(cmpDsl)
            libSp = builder.build()
            libSp!!
        }

    override fun isInitialized(): Boolean = libSp?.let { true } ?: run { false }
}
