package com.sourcepoint.cmplibrary.creation

import android.app.Activity
import com.sourcepoint.cmplibrary.Campaign
import com.sourcepoint.cmplibrary.legislation.gdpr.GDPRConsentLib
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import kotlin.reflect.KProperty

class GDPRConsentLibDelegate(
    private val privacyManagerTab: PrivacyManagerTab = PrivacyManagerTab.PURPOSES,
    private val campaign: Campaign
) {

    private lateinit var lib: GDPRConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): GDPRConsentLib {
        if (!this::lib.isInitialized) {
            lib = makeGdprConsentLib(
                propertyName = campaign.propertyName,
                context = thisRef,
                pmId = campaign.pmId,
                accountId = campaign.accountId,
                propertyId = campaign.propertyId,
                privacyManagerTab = privacyManagerTab
            )
        }
        return lib
    }
}
