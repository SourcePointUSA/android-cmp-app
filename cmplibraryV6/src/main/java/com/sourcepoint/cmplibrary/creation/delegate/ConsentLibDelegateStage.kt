package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLibEnv
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.model.CCPACampaign
import com.sourcepoint.cmplibrary.model.GDPRCampaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import kotlin.reflect.KProperty

class ConsentLibDelegateStage(
    private val privacyManagerTab: PrivacyManagerTabK = PrivacyManagerTabK.PURPOSES,
    private val gdpr: GDPRCampaign? = null,
    private val ccpa: CCPACampaign? = null,
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLibEnv(
                gdpr = gdpr,
                ccpa = ccpa,
                context = thisRef,
                privacyManagerTab = privacyManagerTab,
                env = Env.STAGE
            )
        }
        return libSp
    }
}
