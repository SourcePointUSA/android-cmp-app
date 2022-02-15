package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.model.exposed.SpConfig

@SpDSL
class SpCmpBuilder {

    lateinit var spConfig: SpConfig
    var authId: String? = null
    var ott: Boolean = false
    lateinit var activity: Activity
    lateinit var spClient: SpClient

    fun config(dsl: SpConfigDataBuilder.() -> Unit) {
        spConfig = SpConfigDataBuilder().apply(dsl).build()
    }

    internal fun build(): SpConsentLib {

        if (!this::activity.isInitialized) genericFail("activity param must be initialised!!!")
        if (!this::spConfig.isInitialized) genericFail("spConfig param must be initialised!!!")

        return makeConsentLib(
            spConfig = spConfig,
            activity = activity,
            spClient = spClient
        )
    }
}
