package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.ConsentLib
import com.sourcepoint.cmplibrary.creation.makeGdprConsentLib
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab
import kotlin.reflect.KProperty

class CCPAConsentLibDelegate(
    private val privacyManagerTab: PrivacyManagerTab = PrivacyManagerTab.PURPOSES,
    private val campaign: Campaign
) {
    private lateinit var lib: ConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): ConsentLib {
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
