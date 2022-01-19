package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpCampaign
import com.sourcepoint.cmplibrary.model.exposed.toTParam
import org.junit.Test

class SpConfigDataBuilderTest {

    @Test
    fun `GIVEN a DLS config VERIFY the spConfig object created`() {
        val spConf = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messLanguage = MessageLanguage.ENGLISH
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
    fun `GIVEN a DLS config with default messageLanguage VERIFY the spConfig object created`() {
        val spConf = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            +(CampaignType.GDPR)
            +(CampaignType.CCPA)
        }

        spConf.run {
            accountId.assertEquals(22)
            propertyName.assertEquals("mobile.multicampaign.demo")
            messageLanguage.assertEquals(MessageLanguage.DEFAULT)
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

    @Test
    fun `GIVEN a configuration without messageLanguage RETURN empty string for the messageLanguage field`() {
        val config = config {
            accountId = 22
            propertyName = "mobile.multicampaign.demo"
            messageTimeout = 3000
            +(CampaignType.GDPR)
        }
        config.run {
            messageLanguage.assertEquals(MessageLanguage.DEFAULT)
            propertyName.assertEquals("mobile.multicampaign.demo")
            messageTimeout.assertEquals(3000)
            campaigns.size.assertEquals(1)
        }
    }
}
