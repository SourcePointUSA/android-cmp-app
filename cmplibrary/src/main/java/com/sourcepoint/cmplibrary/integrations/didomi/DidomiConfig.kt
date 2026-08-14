package com.sourcepoint.cmplibrary.integrations.didomi

/**
 * Configuration for Didomi SDK integration.
 *
 * @param apiKey Didomi API key (required)
 * @param noticeId Didomi notice ID (optional - uses default from console if null)
 * @param languageCode Language code for consent UI (optional - uses device language if null)
 * @param localConfigurationPath Path to local Didomi config JSON file in assets (optional)
 * @param remoteConfigurationUrl URL to remote Didomi config JSON (optional)
 */
data class DidomiConfig(
    val apiKey: String,
    val noticeId: String? = null,
    val languageCode: String? = null,
    val localConfigurationPath: String? = null,
    val remoteConfigurationUrl: String? = null
)
