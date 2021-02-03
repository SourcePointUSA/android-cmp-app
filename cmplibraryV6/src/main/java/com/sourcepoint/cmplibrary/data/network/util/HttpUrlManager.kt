package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.legislation.gdpr.PrivacyManagerTabK
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    val inAppUrlMessage: HttpUrl
    val inAppUrlNativeMessage: HttpUrl
    fun ottUrlPm(pmConf: PmUrlConfig): HttpUrl
    fun urlPm(pmConf: PmUrlConfig): HttpUrl
    fun urlUWPm(pmConf: PmUrlConfig, urlLegislation: UrlLegislation): HttpUrl
}

/**
 * Implementation of the [HttpUrlManager] interface
 */
internal object HttpUrlManagerSingleton : HttpUrlManager {

    private const val message = "wrapper/v1/unified/message"
    private const val spHost = "cdn.privacy-mgmt.com"

    val inAppLocalUrlMessage: HttpUrl = HttpUrl.Builder()
        .scheme("http")
        .host("localhost")
        .port(3000)
        .addPathSegments(message)
        .addQueryParameter("env", "localProd")
        .addQueryParameter("inApp", "true")
        .build()

    override val inAppUrlMessage: HttpUrl = inAppLocalUrlMessage

    override val inAppUrlNativeMessage: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(spHost)
            .addPathSegments("wrapper/tcfv2/v1/gdpr")
            .addPathSegments("native-message")
            .addQueryParameter("inApp", "true")
            .build()

    override fun ottUrlPm(pmConf: PmUrlConfig): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(spHost)
        .addPathSegments("privacy-manager-ott")
        .addPathSegments("index.html")
        .addQueryParameter("consentLanguage", pmConf.consentLanguage)
        .addQueryParameter("consentUUID", pmConf.consentUUID)
        .apply {
            if (pmConf.pmTab != PrivacyManagerTabK.DEFAULT) {
                addQueryParameter("pmTab", pmConf.pmTab.key)
            }
        }
        .addQueryParameter("site_id", pmConf.siteId)
        .addQueryParameter("message_id", pmConf.messageId)
        .build()

    override fun urlPm(pmConf: PmUrlConfig): HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host(spHost)
        .addPathSegments("privacy-manager")
        .addPathSegments("index.html")
        .addQueryParameter("consentLanguage", pmConf.consentLanguage)
        .addQueryParameter("consentUUID", pmConf.consentUUID)
        .apply {
            if (pmConf.pmTab != PrivacyManagerTabK.DEFAULT) {
                addQueryParameter("pmTab", pmConf.pmTab.key)
            }
        }
        .addQueryParameter("site_id", pmConf.siteId)
        .addQueryParameter("message_id", pmConf.messageId)
        .build()

    override fun urlUWPm(pmConf: PmUrlConfig, urlLegislation: UrlLegislation): HttpUrl {
        // TODO tests are missing
        return HttpUrl.Builder()
            .scheme("https")
            .host(spHost)
            .addPathSegments(urlLegislation.segment)
            .addPathSegments("privacy-manager")
            .addPathSegments("index.html")
            .addQueryParameter("consentLanguage", pmConf.consentLanguage)
            .addQueryParameter("consentUUID", pmConf.consentUUID)
            .apply {
                if (pmConf.pmTab != PrivacyManagerTabK.DEFAULT) {
                    addQueryParameter("pmTab", pmConf.pmTab.key)
                }
            }
            .addQueryParameter("site_id", pmConf.siteId)
            .addQueryParameter("message_id", pmConf.messageId)
            .build()
    }
}

enum class UrlLegislation(val segment: String) {
    GDPR("segment_gdpr"),
    CCPA("segment_ccpa")
}
