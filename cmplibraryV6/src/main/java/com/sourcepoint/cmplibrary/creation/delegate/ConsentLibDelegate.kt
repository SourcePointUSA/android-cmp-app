package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.model.CCPACampaign
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import kotlin.reflect.KProperty

class ConsentLibDelegate(
    private val privacyManagerTab: PrivacyManagerTabK = PrivacyManagerTabK.PURPOSES,
    private val gdpr: GDPRCampaign? = null,
    private val ccpa: CCPACampaign? = null,
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLib(
                gdpr = gdpr,
                ccpa = ccpa,
                context = thisRef,
                privacyManagerTab = privacyManagerTab
            )
        }
        return libSp
    }
}
