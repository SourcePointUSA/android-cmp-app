package com.sourcepoint.cmplibrary.integrations.didomi

import com.sourcepoint.cmplibrary.model.exposed.*
import io.didomi.sdk.Didomi
import io.didomi.sdk.models.CurrentUserStatus
import org.json.JSONObject

/**
 * Maps Didomi consent data to Sourcepoint consent format.
 */
internal class DidomiConsentMapper {

    fun mapToSourcepoint(): SPConsents {
        val didomi = Didomi.getInstance()
        val userStatus = didomi.currentUserStatus

        return SPConsents(
            gdpr = mapGDPR(userStatus, didomi),
            ccpa = null, // Didomi doesn't support CCPA
            usNat = null, // Didomi doesn't support US NAT
            globalcmp = null,
            preferences = null
        )
    }

    private fun mapGDPR(userStatus: CurrentUserStatus, didomi: Didomi): SPGDPRConsent? {
        if (userStatus.regulation != CurrentUserStatus.Regulation.GDPR) {
            return null
        }

        // Map purpose/vendor grants
        val grants = buildGrants(userStatus, didomi)

        // Get TCF string
        val euconsent = didomi.getUserConsentStatusForVendor("") // TODO: Check correct method

        // Build TCF data
        val tcData = buildTCFData(didomi)

        return SPGDPRConsent(
            vendorGrants = SPGDPRVendorGrants(grants),
            euconsent = euconsent,
            tcfData = tcData,
            uuid = userStatus.userId,
            googleConsentMode = null, // TODO: Map Google Consent Mode if available
            acceptedCategories = emptyList(),
            consentStatus = mapConsentStatus(userStatus),
            applies = true,
            dateCreated = null,
            expirationDate = null,
            webConsentPayload = null,
            grants = grants
        )
    }

    private fun buildGrants(
        userStatus: CurrentUserStatus,
        didomi: Didomi
    ): Map<String, GDPRPurposeGrants> {
        val grants = mutableMapOf<String, GDPRPurposeGrants>()

        // Map Didomi purposes to Sourcepoint grants
        userStatus.purposes.forEach { (purposeId, purposeStatus) ->
            grants[purposeId] = GDPRPurposeGrants(
                purposeGrant = purposeStatus.enabled,
                vendorGrant = false // Not applicable for purposes
            )
        }

        return grants
    }

    private fun buildTCFData(didomi: Didomi): JSONObject {
        // Get IAB TCF data from Didomi
        // Didomi stores this in SharedPreferences with IABTCF_ prefix
        return try {
            val tcfString = didomi.getUserConsentStatusForVendor("") // TODO: Verify method
            JSONObject().apply {
                put("tcString", tcfString)
                // Add other TCF data fields as needed
            }
        } catch (e: Exception) {
            JSONObject()
        }
    }

    private fun mapConsentStatus(userStatus: CurrentUserStatus): ConsentStatus {
        return when {
            userStatus.purposes.values.all { it.enabled } -> ConsentStatus(
                rejectedAny = false,
                rejectedLI = false,
                consentedAll = ConsentedAll(
                    consentedToAll = true,
                    consentedToAny = true,
                    hasConsentData = true
                ),
                granularStatus = null,
                legalBasisChanges = false,
                vendorListAdditions = false,
                hasConsentData = true,
                sellStatus = false,
                shareStatus = false,
                sensitiveDataStatus = false,
                gpcStatus = false
            )
            userStatus.purposes.values.all { !it.enabled } -> ConsentStatus(
                rejectedAny = true,
                rejectedLI = false,
                consentedAll = ConsentedAll(
                    consentedToAll = false,
                    consentedToAny = false,
                    hasConsentData = true
                ),
                granularStatus = null,
                legalBasisChanges = false,
                vendorListAdditions = false,
                hasConsentData = true,
                sellStatus = false,
                shareStatus = false,
                sensitiveDataStatus = false,
                gpcStatus = false
            )
            else -> ConsentStatus(
                rejectedAny = true,
                rejectedLI = false,
                consentedAll = ConsentedAll(
                    consentedToAll = false,
                    consentedToAny = true,
                    hasConsentData = true
                ),
                granularStatus = null,
                legalBasisChanges = false,
                vendorListAdditions = false,
                hasConsentData = true,
                sellStatus = false,
                shareStatus = false,
                sensitiveDataStatus = false,
                gpcStatus = false
            )
        }
    }
}
