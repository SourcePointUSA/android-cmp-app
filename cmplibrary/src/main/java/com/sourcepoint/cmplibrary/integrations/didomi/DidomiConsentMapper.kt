package com.sourcepoint.cmplibrary.integrations.didomi

import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.GoogleConsentMode
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRPurposeGrants
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SPGDPRConsent
import io.didomi.sdk.Didomi
import kotlinx.serialization.json.JsonObject

/**
 * Maps Didomi consent data to Sourcepoint consent format.
 */
internal class DidomiConsentMapper {

    fun mapToSourcepoint(): SPConsents {
        val didomi = Didomi.getInstance()

        return SPConsents(
            gdpr = mapGDPR(didomi),
            ccpa = null, // Didomi doesn't support CCPA
            usNat = null, // Didomi doesn't support US NAT
            globalcmp = null,
            preferences = null
        )
    }

    private fun mapGDPR(didomi: Didomi): SPGDPRConsent? {
        // Create a simple implementation of GDPRConsent interface
        val consentImpl = object : GDPRConsent {
            override val uuid: String? = null // TODO: Get from Didomi
            override val dateCreated: String? = null // TODO: Get from Didomi
            override var euconsent: String = "" // TODO: Get TCF string from Didomi
            override var tcData: Map<String, Any?> = emptyMap() // TODO: Get TCF data from Didomi
            override var grants: Map<String, GDPRPurposeGrants> = emptyMap() // TODO: Map Didomi consents
            override val acceptedCategories: List<String>? = null
            override val applies: Boolean = true
            override val webConsentPayload: JsonObject? = null
            override val consentStatus: ConsentStatus? = null
            override var googleConsentMode: GoogleConsentMode? = null
        }

        return SPGDPRConsent(consent = consentImpl)
    }
}
