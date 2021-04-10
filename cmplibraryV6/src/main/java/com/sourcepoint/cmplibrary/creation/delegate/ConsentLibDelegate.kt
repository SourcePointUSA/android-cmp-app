package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.SpConfig
import kotlin.reflect.KProperty

class ConsentLibDelegate(
    private val spConfig: SpConfig,
    private val privacyManagerTab: PMTab = PMTab.PURPOSES
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLib(
                spConfig = spConfig,
                context = thisRef
            )
        }
        return libSp
    }
}
