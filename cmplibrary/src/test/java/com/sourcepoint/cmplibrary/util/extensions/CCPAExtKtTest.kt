package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.optimized.CCPA
import com.sourcepoint.cmplibrary.data.network.model.optimized.CcpaCS
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import org.junit.Test

internal class CCPAExtKtTest {

    /**
     * Test case that covers the case when:
     * applies == null
     * then the consent string should be == "1---"
     */
    @Test
    fun `generateConsentString - WHEN called with applies as null THEN should return default CCPA consent string`() {
        // GIVEN
        val ccpa = CCPA(
            applies = null,
            consentedAll = null,
            dateCreated = null,
            message = null,
            messageMetaData = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = null,
            status = null,
            type = CampaignType.CCPA,
            url = null,
        )
        val expected = "1---"
        val generated = ccpa.generateConsentString()

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        actual.assertEquals(expected)
        actual.assertEquals(generated)
    }

    /**
     * Test case that covers the case when:
     * applies == false
     * then the consent string should be == "1---"
     */
    @Test
    fun `generateConsentString - WHEN called with applies as false THEN should return default CCPA consent string`() {
        // GIVEN
        val ccpa = CCPA(
            applies = false,
            consentedAll = null,
            dateCreated = null,
            message = null,
            messageMetaData = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = null,
            status = null,
            type = CampaignType.CCPA,
            url = null,
        )
        val expected = "1---"
        val generated = ccpa.generateConsentString()

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        actual.assertEquals(expected)
        actual.assertEquals(generated)
    }

    /**
     * Test case that covers the case when:
     * applies == true
     * status == rejectedAll
     * signedLspa == true
     * then the consent string should be == "1YYY"
     */
    @Test
    fun `generateConsentString - WHEN called with applies==true, status == rejectedAll, signedLspa == true THEN should return 1YYY`() {
        // GIVEN
        val ccpa = CCPA(
            applies = true,
            consentedAll = null,
            dateCreated = null,
            message = null,
            messageMetaData = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = true,
            status = CcpaStatus.rejectedAll,
            type = CampaignType.CCPA,
            url = null,
        )
        val expected = "1YYY"
        val generated = ccpa.generateConsentString()

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        actual.assertEquals(expected)
        actual.assertEquals(generated)
    }

    /**
     * Test case that covers the case when:
     * applies == true
     * status == rejectedSome
     * signedLspa == false
     * then the consent string should be == "1YYN"
     */
    @Test
    fun `generateConsentString - WHEN called with applies==true, status == rejectedSome, signedLspa == false THEN should return 1YYN`() {
        // GIVEN
        val ccpa = CCPA(
            applies = true,
            consentedAll = null,
            dateCreated = null,
            message = null,
            messageMetaData = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = false,
            status = CcpaStatus.rejectedSome,
            type = CampaignType.CCPA,
            url = null,
        )
        val expected = "1YYN"
        val generated = ccpa.generateConsentString()

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        actual.assertEquals(expected)
        actual.assertEquals(generated)
    }

    /**
     * Test case that covers the case when:
     * applies == true
     * status == consentedAll (basically, anything except rejectedAll and rejectedSome)
     * signedLspa == false
     * then the consent string should be == "1YNN"
     */
    @Test
    fun `generateConsentString - WHEN called with applies==true, status == consentedAll, signedLspa == false THEN should return 1YNN`() {
        // GIVEN
        val ccpa = CCPA(
            applies = true,
            consentedAll = null,
            dateCreated = null,
            message = null,
            messageMetaData = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = false,
            status = CcpaStatus.consentedAll,
            type = CampaignType.CCPA,
            url = null,
        )
        val expected = "1YNN"
        val generated = ccpa.generateConsentString()

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        actual.assertEquals(expected)
        actual.assertEquals(generated)
    }
}