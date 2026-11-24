package com.sourcepoint.cmplibrary.integrations.didomi

import com.sourcepoint.cmplibrary.assertEquals
import com.sourcepoint.cmplibrary.assertNotNull
import com.sourcepoint.cmplibrary.assertNull
import org.junit.Test

class DidomiConfigTest {

    @Test
    fun `GIVEN DidomiConfig with required params VERIFY config is created`() {
        val config = DidomiConfig(
            apiKey = "test-api-key",
            noticeId = "test-notice-id"
        )

        config.apiKey.assertEquals("test-api-key")
        config.noticeId.assertEquals("test-notice-id")
        config.languageCode.assertNull()
        config.localConfigurationPath.assertNull()
        config.remoteConfigurationUrl.assertNull()
    }

    @Test
    fun `GIVEN DidomiConfig with only apiKey VERIFY optional params are null`() {
        val config = DidomiConfig(
            apiKey = "test-api-key"
        )

        config.apiKey.assertEquals("test-api-key")
        config.noticeId.assertNull()
        config.languageCode.assertNull()
        config.localConfigurationPath.assertNull()
        config.remoteConfigurationUrl.assertNull()
    }

    @Test
    fun `GIVEN DidomiConfig with all params VERIFY all fields are set`() {
        val config = DidomiConfig(
            apiKey = "test-api-key",
            noticeId = "test-notice-id",
            languageCode = "en",
            localConfigurationPath = "assets/didomi-config.json",
            remoteConfigurationUrl = "https://example.com/didomi-config.json"
        )

        config.assertNotNull()
        config.apiKey.assertEquals("test-api-key")
        config.noticeId.assertEquals("test-notice-id")
        config.languageCode.assertEquals("en")
        config.localConfigurationPath.assertEquals("assets/didomi-config.json")
        config.remoteConfigurationUrl.assertEquals("https://example.com/didomi-config.json")
    }

    @Test
    fun `GIVEN DidomiConfig data class VERIFY copy works correctly`() {
        val original = DidomiConfig(
            apiKey = "original-api-key",
            noticeId = "original-notice-id"
        )

        val modified = original.copy(apiKey = "modified-api-key")

        modified.apiKey.assertEquals("modified-api-key")
        modified.noticeId.assertEquals("original-notice-id")
        original.apiKey.assertEquals("original-api-key")
    }

    @Test
    fun `GIVEN two DidomiConfigs with same values VERIFY equality`() {
        val config1 = DidomiConfig(
            apiKey = "test-api-key",
            noticeId = "test-notice-id"
        )

        val config2 = DidomiConfig(
            apiKey = "test-api-key",
            noticeId = "test-notice-id"
        )

        config1.assertEquals(config2)
    }
}
