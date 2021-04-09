package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.model.SpProperty
import kotlin.reflect.KProperty

class ConsentLibDelegateStage(
    private val privacyManagerTab: PrivacyManagerTabK = PrivacyManagerTabK.PURPOSES,
    private val spProperty: SpProperty,
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLib(
                spProperty = spProperty,
                context = thisRef,
                privacyManagerTab = privacyManagerTab
            )
        }
        return libSp
    }
}
