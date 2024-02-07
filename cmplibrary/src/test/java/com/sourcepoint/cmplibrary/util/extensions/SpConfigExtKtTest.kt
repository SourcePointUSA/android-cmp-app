package com.sourcepoint.cmplibrary.util.extensions

import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.creation.ConfigOption
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.SpGppOptionBinary.NO
import com.sourcepoint.cmplibrary.model.exposed.SpGppOptionTernary.YES
import kotlinx.serialization.json.jsonObject
import org.junit.Test

class SpConfigExtKtTest {

    private val ccpaCampaign = SpCampaign(
        CampaignType.CCPA,
        listOf(TargetingParam("location", "EU"))
    )

    private val gdprCampaign = SpCampaign(
        CampaignType.GDPR,
        listOf(TargetingParam("location", "EU"))
    )

    private val usnatCampaign = SpCampaign(
        CampaignType.USNAT,
        listOf(TargetingParam("location", "EU"))
    )

    private val usnatCampaignSupportLegacyUspstring = SpCampaign(
        campaignType = CampaignType.USNAT,
        targetingParams = emptyList(),
        configParams = setOf(ConfigOption.SUPPORT_LEGACY_USPSTRING)
    )

    private val spConfig = SpConfig(
        22,
        "carm.uw.con",
        listOf(ccpaCampaign),
        MessageLanguage.ENGLISH,
        propertyId = 1234,
        messageTimeout = 3000,
    )

    @Test
    fun `GIVEN a spConfig with GPP custom config CHECK that getGppCustomOption return an obj`() {
        spConfig.copy(spGppConfig = SpGppConfig(NO, YES, YES)).getGppCustomOption().assertNotNull()
    }

    @Test
    fun `GIVEN a spConfig without GPP custom config CHECK that getGppCustomOption return null`() {
        spConfig.getGppCustomOption().assertNull()
    }

    @Test
    fun `GIVEN a gdpr spConfig without CCPA CHECK that getGppCustomOption return null`() {
        spConfig
            .copy(campaigns = listOf(gdprCampaign), spGppConfig = SpGppConfig(NO, YES, YES))
            .getGppCustomOption().assertNull()
    }

    @Test
    fun `GIVEN a usnat spConfig CHECK that getGppCustomOption return null`() {
        spConfig
            .copy(campaigns = listOf(usnatCampaign), spGppConfig = SpGppConfig(NO, YES, YES))
            .getGppCustomOption().assertNull()
    }

    @Test
    fun `GIVEN a usnat with SUPPORT_LEGACY_USPSTRING CHECK that getGppCustomOption return the uspstring element`() {
        spConfig
            .copy(campaigns = listOf(usnatCampaignSupportLegacyUspstring))
            .getGppCustomOption()!!
            .jsonObject["uspString"]
            .assertNotNull()
    }

    @Test
    fun `GIVEN a usnat without SUPPORT_LEGACY_USPSTRING CHECK that getGppCustomOption DOES NOT RETURN the uspstring element`() {
        spConfig
            .copy(campaigns = listOf(usnatCampaign))
            .getGppCustomOption()
            .assertNull()
    }
}
