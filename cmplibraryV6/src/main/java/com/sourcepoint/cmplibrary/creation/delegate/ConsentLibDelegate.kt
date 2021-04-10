package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import com.sourcepoint.cmplibrary.model.SpProperty
import kotlin.reflect.KProperty

class ConsentLibDelegate(
    private val spProperty: SpProperty,
    private val privacyManagerTab: PrivacyManagerTabK = PrivacyManagerTabK.PURPOSES
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLib(
                context = thisRef,
                privacyManagerTab = privacyManagerTab,
                spProperty = spProperty
            )
        }
        return libSp
    }
}
