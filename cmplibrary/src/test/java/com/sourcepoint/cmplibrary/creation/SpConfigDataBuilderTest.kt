package com.sourcepoint.cmplibrary.creation

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertNull
import com.sourcepoint.cmplibrary.integrations.didomi.DidomiConfig
import com.sourcepoint.cmplibrary.model.MessageLanguage
import org.junit.Test

class SpConfigDataBuilderTest {
    @Test
    fun `GIVEN a DLS config VERIFY the spConfig object created`() {
        val spConf = config {
            messLanguage = MessageLanguage.SPANISH
            messageTimeout = 3000
        }

        spConf.run {
            messageLanguage.assertEquals(MessageLanguage.SPANISH)
            messageTimeout.assertEquals(3000L)
        }
    }

    @Test
    fun `GIVEN an empty DLS config VERIFY the defaults`() {
        val spConf = config { }

        spConf.run {
            messageLanguage.assertEquals(MessageLanguage.ENGLISH)
            messageTimeout.assertEquals(5000L)
        }
    }

    @Test
    fun `GIVEN config without Didomi VERIFY didomiConfig is null`() {
        config { }.didomiConfig.assertNull()
    }

    @Test
    fun `GIVEN config with Didomi VERIFY didomiConfig is set`() {
        val didomiConfig = DidomiConfig(
            apiKey = "test-api-key",
            noticeId = "test-notice-id"
        )

        val spConf = config {
            addDidomiConfig(didomiConfig)
        }

        spConf.didomiConfig.assertNotNull()
        spConf.didomiConfig?.apiKey.assertEquals("test-api-key")
        spConf.didomiConfig?.noticeId.assertEquals("test-notice-id")
    }

    @Test
    fun `GIVEN Didomi config with optional params VERIFY all fields are set`() {
        val didomiConfig = DidomiConfig(
            apiKey = "test-api-key",
            noticeId = "test-notice-id",
            languageCode = "en",
            localConfigurationPath = "/path/to/config.json",
            remoteConfigurationUrl = "https://example.com/config.json"
        )

        val spConf = config {
            addDidomiConfig(didomiConfig)
        }

        spConf.didomiConfig.assertNotNull()
        spConf.didomiConfig?.run {
            apiKey.assertEquals("test-api-key")
            noticeId.assertEquals("test-notice-id")
            languageCode.assertEquals("en")
            localConfigurationPath.assertEquals("/path/to/config.json")
            remoteConfigurationUrl.assertEquals("https://example.com/config.json")
        }
    }
}
