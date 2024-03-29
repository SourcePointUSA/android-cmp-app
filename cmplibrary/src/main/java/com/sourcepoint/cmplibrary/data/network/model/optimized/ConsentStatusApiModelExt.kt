package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsentInternal
import com.sourcepoint.cmplibrary.model.exposed.UsNatConsentInternal
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.extensions.toMapOfAny
import kotlinx.serialization.encodeToString
import org.json.JSONObject

internal fun GdprCS.toGDPRUserConsent(): GDPRConsentInternal {
    return GDPRConsentInternal(
        uuid = uuid,
        applies = applies ?: false,
        tcData = TCData?.toMapOfAny() ?: emptyMap(),
        grants = grants ?: emptyMap(),
        euconsent = euconsent ?: "",
        acceptedCategories = categories,
        consentStatus = consentStatus,
        childPmId = null,
        thisContent = JSONObject(),
        webConsentPayload = webConsentPayload,
        googleConsentMode = googleConsentMode
    )
}

internal fun CcpaCS.toCCPAConsentInternal(): CCPAConsentInternal {
    return CCPAConsentInternal(
        uuid = uuid,
        applies = applies ?: false,
        gppData = gppData?.toMapOfAny() ?: emptyMap(),
        status = status,
        childPmId = null,
        rejectedVendors = rejectedVendors ?: emptyList(),
        rejectedCategories = rejectedCategories ?: emptyList(),
        thisContent = JSONObject(),
        signedLspa = signedLspa,
        webConsentPayload = webConsentPayload,
        uspstring = this.uspstring ?: CCPAConsent.DEFAULT_USPSTRING
    )
}

internal fun USNatConsentData.toUsNatConsentInternal(): UsNatConsentInternal = UsNatConsentInternal(
    applies = applies ?: false,
    gppData = gppData?.toMapOfAny() ?: emptyMap(),
    consentStatus = consentStatus,
    vendors = vendors,
    categories = categories,
    consentStrings = consentStrings ?: emptyList(),
    dateCreated = dateCreated,
    uuid = uuid,
    webConsentPayload = webConsentPayload,
    url = url,
)

internal fun USNatConsentData.stringify(): String? {
    return check { JsonConverter.converter.encodeToString(this) }.getOrNull()
}
