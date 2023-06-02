package com.sourcepoint.cmplibrary.model

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import org.json.JSONObject
import org.junit.Test

class SPConsentsTest {

    /**
     * This test case verifies that CCPA uuid is being exposed to the external user via CCPAConsent
     * interface
     */
    @Test
    fun `UUID from CCPAConsent - should be exposed`() {
        // GIVEN
        val mockUUID = "ccpa_uuid"
        val mockCCPAConsentInternal = CCPAConsentInternal(
            uuid = mockUUID,
            rejectedCategories = listOf(),
            rejectedVendors = listOf(),
            status = CcpaStatus.consentedAll,
            childPmId = "child_pm_id",
            applies = true,
            thisContent = JSONObject(),
        )

        // WHEN
        val mockCCPAConsent = mockCCPAConsentInternal as CCPAConsent

        // THEN
        mockCCPAConsent.uuid.assertNotNull()
        mockCCPAConsent.uuid.assertEquals(mockUUID)
    }

    /**
     * This test case verifies that GDPR uuid is being exposed to the external user via GDPRConsent
     * interface
     */
    @Test
    fun `UUID from GDPRConsent - should be exposed`() {
        // GIVEN
        val mockUUID = "gdpr_uuid"
        val mockGDPRConsentInternal = GDPRConsentInternal(
            euconsent = "eu_consent",
            uuid = mockUUID,
            tcData = mapOf(),
            grants = mapOf(),
            acceptedCategories = listOf(),
            applies = true,
            childPmId = "child_pm_id",
            thisContent = JSONObject(),
        )

        // WHEN
        val mockGDPRConsent = mockGDPRConsentInternal as GDPRConsent

        // THEN
        mockGDPRConsent.uuid.assertNotNull()
        mockGDPRConsent.uuid.assertEquals(mockUUID)
    }
}
