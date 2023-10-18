package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.data.network.model.optimized.CCPA
import com.sourcepoint.cmplibrary.data.network.model.optimized.toCcpaCS
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import org.junit.Test

internal class SpUtilsTest {

    /**
     * Test case that covers the case when:
     * applies == null
     * then the consent string should be == "1---"
     */
    @Test
    fun `generateCcpaUspString - WHEN called with applies as null THEN should return default CCPA consent string`() {
        // GIVEN
        val ccpa = createCCPA(
            applies = null,
        )
        val expected = "1---"
        val generated = updateCcpaUspString(ccpa.toCcpaCS(null))

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        generated.assertEquals(expected)
    }

    /**
     * Test case that covers the case when:
     * applies == false
     * then the consent string should be == "1---"
     */
    @Test
    fun `generateCcpaUspString - WHEN called with applies as false THEN should return default CCPA consent string`() {
        // GIVEN
        val ccpa = createCCPA(applies = false)
        val expected = "1---"
        val generated = updateCcpaUspString(ccpa.toCcpaCS(false))

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        generated.assertEquals(expected)
    }

    /**
     * Test case that covers the case when:
     * applies == true
     * status == rejectedAll
     * signedLspa == true
     * then the consent string should be == "1YYY"
     */
    @Test
    fun `generateCcpaUspString - WHEN called with applies==true, status == rejectedAll, signedLspa == true THEN should return 1YYY`() {
        // GIVEN
        val ccpa = createCCPA(
            applies = true,
            ccpaStatus = CcpaStatus.rejectedAll,
            signedLspa = true,
        )

        val expected = "1YYY"
        val generated = updateCcpaUspString(ccpa.toCcpaCS(true))

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        generated.assertEquals(expected)
    }

    /**
     * Test case that covers the case when:
     * applies == true
     * status == rejectedSome
     * signedLspa == false
     * then the consent string should be == "1YYN"
     */
    @Test
    fun `generateCcpaUspString - WHEN called with applies==true, status == rejectedSome, signedLspa == false THEN should return 1YYN`() {
        // GIVEN
        val ccpa = createCCPA(
            applies = true,
            ccpaStatus = CcpaStatus.rejectedSome,
            signedLspa = false,
        )
        val expected = "1YYN"
        val generated = updateCcpaUspString(ccpa.toCcpaCS(true))

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        generated.assertEquals(expected)
    }

    /**
     * Test case that covers the case when:
     * applies == true
     * status == consentedAll (basically, anything except rejectedAll and rejectedSome)
     * signedLspa == false
     * then the consent string should be == "1YNN"
     */
    @Test
    fun `generateCcpaUspString - WHEN called with applies==true, status == consentedAll, signedLspa == false THEN should return 1YNN`() {
        // GIVEN
        val ccpa = createCCPA(
            applies = true,
            ccpaStatus = CcpaStatus.consentedAll,
            signedLspa = false,
        )
        val expected = "1YNN"
        val generated = updateCcpaUspString(ccpa.toCcpaCS(true))

        // WHEN
        val actual = ccpa.uspstring

        // THEN
        generated.assertEquals(expected)
    }

    private fun createCCPA(
        applies: Boolean? = null,
        ccpaStatus: CcpaStatus? = null,
        signedLspa: Boolean? = null,
    ): CCPA = CCPA(
        consentedAll = null,
        dateCreated = null,
        message = null,
        messageMetaData = null,
        newUser = null,
        rejectedAll = null,
        rejectedCategories = null,
        rejectedVendors = null,
        signedLspa = signedLspa,
        status = ccpaStatus,
        type = CampaignType.CCPA,
        url = null,
        webConsentPayload = null
    )
}
