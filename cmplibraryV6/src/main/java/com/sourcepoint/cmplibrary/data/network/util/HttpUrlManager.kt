package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    val inAppUrlMessage: HttpUrl
    val inAppUrlNativeMessage: HttpUrl
    val sendConsentUrlOld: HttpUrl
    val sendGdprConsentUrl: HttpUrl
    val sendCcpaConsentUrl: HttpUrl
    fun sendConsentUrl(legislation: Legislation): HttpUrl
    fun ottUrlPm(pmConf: PmUrlConfig): HttpUrl
    fun urlPm(pmConf: PmUrlConfig): HttpUrl
    fun urlUWPm(pmConf: PmUrlConfig, urlLegislation: UrlLegislation): HttpUrl
    fun urlURenderingAppProd(): HttpUrl
    fun urlURenderingAppStage(): HttpUrl
    fun urlURenderingAppLocal(): HttpUrl
}

/**
 * Implementation of the [HttpUrlManager] interface
 */
internal object HttpUrlManagerSingleton : HttpUrlManager {

    private const val message = "wrapper/v1/unified/message"
    private const val spHost = "cdn.privacy-mgmt.com"
    private const val spHostProd = "notice.sp-prod.net"
    private const val spHostStage = "notice.sp-stage.net"

    val inAppLocalUrlMessage: HttpUrl = HttpUrl.Builder()
        .scheme("http")
        .host("192.168.1.11")
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

    override val sendConsentUrlOld: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(spHost)
            .addPathSegments("wrapper/tcfv2/v1/gdpr")
            .addPathSegments("consent")
            .addQueryParameter("inApp", "true")
            .build()

    override val sendGdprConsentUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("http")
            .host("192.168.1.11")
            .port(3000)
            .addPathSegments("wrapper/tcfv2/v1/gdpr")
            .addPathSegments("consent")
            .addQueryParameter("env", "localProd")
            .addQueryParameter("inApp", "true")
            .addQueryParameter("sdkVersion", "AndroidLocal")
            .build()

    override val sendCcpaConsentUrl: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("http")
            .host("192.168.1.11")
            .port(3000)
            .addPathSegments("wrapper/tcfv2/v1/ccpa")
            .addPathSegments("consent")
            .addQueryParameter("env", "localProd")
            .addQueryParameter("inApp", "true")
            .addQueryParameter("sdkVersion", "AndroidLocal")
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
        // https://notice.sp-prod.net?preload_message=true
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

    override fun urlURenderingAppProd(): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host(spHostProd)
            .addQueryParameter("preload_message", "true")
            .build()
    }

    override fun urlURenderingAppStage(): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host(spHostStage)
            .addQueryParameter("preload_message", "true")
            .build()
    }

    override fun urlURenderingAppLocal(): HttpUrl {
        // "http://192.168.1.11:8080/?preload_message=true"
        return HttpUrl.Builder()
            .scheme("http")
            .host("192.168.1.11")
            .port(8080)
            .addQueryParameter("preload_message", "true")
            .build()
    }

    override fun sendConsentUrl(legislation: Legislation): HttpUrl {
        return when (legislation) {
            Legislation.GDPR -> sendGdprConsentUrl
            Legislation.CCPA -> sendCcpaConsentUrl
        }
    }
}

enum class UrlLegislation(val segment: String) {
    GDPR("segment_gdpr"),
    CCPA("segment_ccpa")
}
