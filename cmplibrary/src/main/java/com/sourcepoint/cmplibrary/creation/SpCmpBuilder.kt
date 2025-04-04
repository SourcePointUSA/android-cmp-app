package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManager
import com.sourcepoint.cmplibrary.data.network.connection.ConnectionManagerImpl
import com.sourcepoint.cmplibrary.model.exposed.SpConfig

@SpDSL
class SpCmpBuilder {

    lateinit var spConfig: SpConfig
    var authId: String? = null
    lateinit var activity: Activity
    lateinit var spClient: SpClient
    var connectionManager: ConnectionManager? = null

    fun config(dsl: SpConfigDataBuilder.() -> Unit) {
        spConfig = SpConfigDataBuilder().apply(dsl).build()
    }

    internal fun build(): SpConsentLib {

        if (!this::activity.isInitialized) throw RuntimeException("activity param must be initialised!!!")
        if (!this::spConfig.isInitialized) throw RuntimeException("spConfig param must be initialised!!!")

        return makeConsentLib(
            spConfig = spConfig,
            activity = activity,
            spClient = spClient,
            connectionManager = connectionManager ?: ConnectionManagerImpl(activity.applicationContext)
        )
    }
}
