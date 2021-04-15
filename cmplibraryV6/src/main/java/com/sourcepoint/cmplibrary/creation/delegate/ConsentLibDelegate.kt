package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.PMTab
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import kotlin.reflect.KProperty

class ConsentLibDelegate(
    private val spConfig: SpConfig,
    private val privacyManagerTab: PMTab = PMTab.PURPOSES,
    private val messageLanguage: MessageLanguage
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLib(
                spConfig = spConfig,
                context = thisRef,
                messageLanguage = messageLanguage
            )
        }
        return libSp
    }
}
