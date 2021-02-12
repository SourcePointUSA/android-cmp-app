package com.sourcepoint.cmplibrary.creation.delegate

import android.app.Activity
import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.creation.makeConsentLib
import com.sourcepoint.cmplibrary.model.Campaign
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import kotlin.reflect.KProperty

class ConsentLibDelegate(
    private val privacyManagerTab: PrivacyManagerTabK = PrivacyManagerTabK.PURPOSES,
    private val campaign: Campaign
) {

    private lateinit var libSp: SpConsentLib

    operator fun getValue(thisRef: Activity, property: KProperty<*>): SpConsentLib {
        if (!this::libSp.isInitialized) {
            libSp = makeConsentLib(
                propertyName = campaign.propertyName,
                context = thisRef,
                pmId = campaign.pmId,
                accountId = campaign.accountId,
                propertyId = campaign.propertyId,
                privacyManagerTab = privacyManagerTab
            )
        }
        return libSp
    }
}
