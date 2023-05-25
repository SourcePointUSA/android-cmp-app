package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.optimized.CcpaCS
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import org.junit.Test

internal class CcpaCSExtKtTest {

    /**
     * Test case that covers the case when:
     * applies == null
     * then the consent string should be == "1---"
     */
    @Test
    fun `generateConsentString - WHEN called with applies as null THEN should return default CCPA consent string`() {
        // GIVEN
        val ccpaCS = CcpaCS(
            actions = null,
            applies = null,
            ccpaApplies = null,
            consentedAll = null,
            cookies = null,
            dateCreated = null,
            gpcEnabled = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = false,
            status = null,
            uspstring = null,
            uuid = null,
        )
        val expected = "1---"

        // WHEN
        val actual = ccpaCS.generateConsentString()

        // THEN
        actual.assertEquals(expected)
    }

    /**
     * Test case that covers the case when:
     * applies == false
     * then the consent string should be == "1---"
     */
    @Test
    fun `generateConsentString - WHEN called with applies as false THEN should return default CCPA consent string`() {
        // GIVEN
        val ccpaCS = CcpaCS(
            actions = null,
            applies = false,
            ccpaApplies = null,
            consentedAll = null,
            cookies = null,
            dateCreated = null,
            gpcEnabled = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = false,
            status = null,
            uspstring = "",
            uuid = null,
        )
        val expected = "1---"

        // WHEN
        val actual = ccpaCS.generateConsentString()

        // THEN
        actual.assertEquals(expected)
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
        val ccpaCS = CcpaCS(
            actions = null,
            applies = true,
            ccpaApplies = null,
            consentedAll = null,
            cookies = null,
            dateCreated = null,
            gpcEnabled = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = true,
            status = CcpaStatus.rejectedAll,
            uspstring = "",
            uuid = null,
        )
        val expected = "1YYY"

        // WHEN
        val actual = ccpaCS.generateConsentString()

        // THEN
        actual.assertEquals(expected)
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
        val ccpaCS = CcpaCS(
            actions = null,
            applies = true,
            ccpaApplies = null,
            consentedAll = null,
            cookies = null,
            dateCreated = null,
            gpcEnabled = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = false,
            status = CcpaStatus.rejectedSome,
            uspstring = "",
            uuid = null,
        )
        val expected = "1YYN"

        // WHEN
        val actual = ccpaCS.generateConsentString()

        // THEN
        actual.assertEquals(expected)
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
        val ccpaCS = CcpaCS(
            actions = null,
            applies = true,
            ccpaApplies = null,
            consentedAll = null,
            cookies = null,
            dateCreated = null,
            gpcEnabled = null,
            newUser = null,
            rejectedAll = null,
            rejectedCategories = null,
            rejectedVendors = null,
            signedLspa = false,
            status = CcpaStatus.consentedAll,
            uspstring = "",
            uuid = null,
        )
        val expected = "1YNN"

        // WHEN
        val actual = ccpaCS.generateConsentString()

        // THEN
        actual.assertEquals(expected)
    }
}