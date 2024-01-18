package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertFalse
import com.sourcepoint.cmplibrary.assertTrue
import com.sourcepoint.cmplibrary.creation.ConfigOption.TRANSITION_CCPA_AUTH
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.toTParam
import com.sourcepoint.cmplibrary.util.extensions.hasTransitionCCPAAuth
import org.junit.Test

class SpConfigDataBuilderTest {

    @Test
    fun `GIVEN a USNAT config with CCPA migration VERIFY the spConfig object has the TRANSITION_CCPA_AUTH config`() {
        val spConf = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            propertyId = 16893
            +(CampaignType.GDPR)
            +(CampaignType.USNAT to setOf(TRANSITION_CCPA_AUTH))
        }

        spConf.hasTransitionCCPAAuth().assertTrue()
    }

    @Test
    fun `GIVEN a USNAT config without CCPA migration VERIFY the spConfig object doesn't have the TRANSITION_CCPA_AUTH config`() {
        val spConf = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            propertyId = 16893
            +(CampaignType.GDPR)
            +(CampaignType.USNAT)
        }

        spConf.hasTransitionCCPAAuth().assertFalse()
    }

    @Test
    fun `GIVEN a DLS config VERIFY the spConfig object created`() {
        val spConf = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            propertyId = 16893
            +(CampaignType.GDPR)
            +(CampaignType.CCPA)
        }

        spConf.run {
            accountId.assertEquals(22)
            propertyName.assertEquals("mobile.multicampaign.demo")
            messageLanguage.assertEquals(MessageLanguage.ENGLISH)
            campaigns.also { l ->
                l.contains(SpCampaign(CampaignType.GDPR, emptyList()))
                l.contains(SpCampaign(CampaignType.CCPA, emptyList()))
                l.size.assertEquals(2)
            }
        }
    }

    @Test
    fun `GIVEN a DLS config with targetingParams VERIFY the spConfig object created`() {
        val spConf = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
            propertyId = 16893
            +((CampaignType.GDPR) to listOf(("location" to "EU")))
            +((CampaignType.CCPA) to listOf(("location" to "US")))
        }

        spConf.run {
            accountId.assertEquals(22)
            propertyName.assertEquals("mobile.multicampaign.demo")
            messageLanguage.assertEquals(MessageLanguage.ENGLISH)
            campaigns.also { l ->
                l.contains(SpCampaign(CampaignType.GDPR, listOf(("location" to "EU").toTParam())))
                l.contains(SpCampaign(CampaignType.CCPA, listOf(("location" to "US").toTParam())))
                l.size.assertEquals(2)
            }
        }
    }
}
