package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.SpClient
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.data.network.converter.genericFail
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import java.lang.ref.WeakReference

@SpDSL
class SpCmpBuilder {

    private lateinit var spConfig: SpConfig
    var authId: String? = null
    lateinit var weakReference: WeakReference<Activity>
    var ott: Boolean = false
    lateinit var privacyManagerTab: PMTab
    lateinit var messageLanguage: MessageLanguage
    lateinit var activity: Activity
    lateinit var spClient: SpClient

    fun config(dsl: SpConfigDataBuilder.() -> Unit) {
        spConfig = SpConfigDataBuilder().apply(dsl).build()
    }

    internal fun build(): SpConsentLib {

        if (!this::privacyManagerTab.isInitialized) genericFail("privacyManagerTab param must be initialised!!!")
        if (!this::activity.isInitialized) genericFail("activity param must be initialised!!!")
        if (!this::spConfig.isInitialized) genericFail("spConfig param must be initialised!!!")
        if (!this::messageLanguage.isInitialized) {
            messageLanguage = MessageLanguage.ENGLISH
        }

        return makeConsentLib(
            spConfig = spConfig,
            activity = activity,
            messageLanguage = messageLanguage
        ).also { it.spClient = spClient }
    }
}
