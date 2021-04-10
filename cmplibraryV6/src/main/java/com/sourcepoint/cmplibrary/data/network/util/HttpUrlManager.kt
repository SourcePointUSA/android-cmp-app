package com.sourcepoint.cmplibrary.data.network.util

import com.sourcepoint.cmplibrary.data.network.model.PmUrlConfig
import com.sourcepoint.cmplibrary.data.network.util.Env.PROD
import com.sourcepoint.cmplibrary.data.network.util.Env.STAGE
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.ActionType
import com.sourcepoint.cmplibrary.model.PrivacyManagerTabK
import okhttp3.HttpUrl

/**
 * Component responsible of building and providing the URLs
 */
internal interface HttpUrlManager {
    fun sendConsentUrl(actionType: ActionType, env: Env, legislation: Legislation): HttpUrl
    fun pmUrl(env: Env, legislation: Legislation, pmConfig: PmUrlConfig?): HttpUrl
    fun inAppMessageUrl(env: Env): HttpUrl
    fun urlURenderingApp(env: Env): HttpUrl

    fun ottUrlPm(pmConf: PmUrlConfig): HttpUrl
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

    val inAppUrlMessage1203: HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("fake-wrapper-api.herokuapp.com")
        .addPathSegments("all/v1/multi-campaign")
        .build()

    // https://cdn.sp-stage.net/wrapper/v2/messages?env=stage
//    override val inAppUrlMessage: HttpUrl = HttpUrl.Builder()
//        .scheme("https")
//        .host("cdn.sp-stage.net")
//        .addPathSegments("wrapper/v2/messages")
//        .addQueryParameter("env", "localProd")
//        .build()

    val inAppUrlMessageStage: HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("cdn.sp-stage.net")
        .addPathSegments("wrapper/v2/messages")
        .addQueryParameter("env", "stage")
        .build()

    val inAppUrlMessageProd: HttpUrl = HttpUrl.Builder()
        .scheme("https")
        .host("cdn.sp-stage.net") // TODO do we have prod env??
        .addPathSegments("wrapper/v2/messages")
        .addQueryParameter("env", "stage")
        .build()

    val inAppUrlNativeMessage: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host(spHost)
            .addPathSegments("wrapper/tcfv2/v1/gdpr")
            .addPathSegments("native-message")
            .addQueryParameter("inApp", "true")
            .build()

    // https://cdn.privacy-mgmt.com/wrapper/tcfv2/v1/gdpr/consent?inApp=true
    val sendGdprConsentUrl: HttpUrl
        get() = sendGdprConsentUrlStage

//    https://cdn.sp-stage.net/wrapper/v2/messages/gdpr/11?env=stage
    val sendGdprConsentUrlStage: HttpUrl
        get() = HttpUrl.Builder()
            .scheme("https")
            .host("cdn.sp-stage.net")
            .addPathSegments("wrapper/tcfv2/v1/gdpr/consent")
            .addQueryParameter("inApp", "true")
            .addQueryParameter("env", "stage")
            .build()

    val sendLocalGdprConsentUrl: HttpUrl
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

    val sendCcpaConsentUrl: HttpUrl
        get() {
            return HttpUrl.Builder()
                .scheme("http")
                .host("192.168.1.11")
                .port(3000)
                .addPathSegments("wrapper/tcfv2/v1/ccpa")
                .addPathSegments("consent")
                .addQueryParameter("env", "localProd")
                .addQueryParameter("inApp", "true")
                .addQueryParameter("sdkVersion", "AndroidLocal")
                .build()
        }

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

    fun urlPm(pmConf: PmUrlConfig): HttpUrl = HttpUrl.Builder()
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

    fun urlPmGdpr(): HttpUrl = HttpUrl.parse("https://cdn.privacy-mgmt.com/privacy-manager/index.html?consentLanguage=&site_id=7639&message_id=122058&consentUUID=170ea8dc-54e4-4f65-9914-6abe83106225")!!

    fun urlPmCcpa(): HttpUrl = HttpUrl.parse("https://ccpa-inapp-pm.sp-prod.net?ccpa_origin=https://ccpa-service.sp-prod.net&privacy_manager_id=5df9105bcf42027ce707bb43&ccpaUUID=76c950be-45be-40ce-878b-c7bcf091722d&site_id=6099")!!

    fun urlUWPm(pmConf: PmUrlConfig, urlLegislation: UrlLegislation): HttpUrl {
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

    override fun urlURenderingApp(env: Env): HttpUrl = when (env) {
        STAGE -> urlURenderingAppStage()
        PROD -> urlURenderingAppProd()
    }

    fun urlURenderingAppProd(): HttpUrl {
        return HttpUrl.Builder()
            .scheme("https")
            .host(spHostProd)
            .addQueryParameter("preload_message", "true")
            .build()
    }

    fun urlURenderingAppStage(): HttpUrl {
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

//    override fun sendConsentUrl(legislation: Legislation, actionType: String): HttpUrl {
//        return when (legislation) {
//            Legislation.GDPR -> sendGdprConsentUrl
//            Legislation.CCPA -> sendCcpaConsentUrl(actionType)
//        }
//    }

    override fun inAppMessageUrl(env: Env): HttpUrl = when (env) {
        STAGE -> inAppUrlMessageStage
        PROD -> inAppUrlMessageProd
    }

    fun sendCcpaConsentUrlStage(actionType: Int): HttpUrl {
        // https://wrapper-api.sp-prod.net/ccpa/consent/{action}

        return HttpUrl.Builder()
            .scheme("https")
            .host("wrapper-api.sp-stage.net")
            .addPathSegments("ccpa/consent/$actionType")
            .build()
    }

    fun sendCcpaConsentUrlProd(actionType: Int): HttpUrl {
        // https://cdn.sp-stage.net/wrapper/v2/messages/ccpa/11?env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host("cdn.sp-stage.net")
            .addPathSegments("wrapper/v2/messages/ccpa/$actionType")
            .addQueryParameter("env", "stage")
            .build()
    }
//    https://cdn.sp-stage.net/wrapper/v2/messages/gdpr/:actionType?env=stage
    fun sendGdprConsentUrlProd(actionType: Int): HttpUrl {
        // https://cdn.sp-stage.net/wrapper/v2/messages/ccpa/11?env=stage
        return HttpUrl.Builder()
            .scheme("https")
            .host("cdn.sp-stage.net")
            .addPathSegments("wrapper/v2/messages/gdpr/$actionType")
            .addQueryParameter("env", "stage")
            .build()
    }

    override fun sendConsentUrl(actionType: ActionType, env: Env, legislation: Legislation): HttpUrl {
        return when (legislation) {
            Legislation.CCPA -> {
                when (env) {
                    PROD -> HttpUrl.Builder()
                        .scheme("https")
                        .host("fake-wrapper-api.herokuapp.com")
                        .addPathSegments("all/v1/consent/$actionType")
                        .build() // sendCcpaConsentUrlStage(actionType = actionType.code)
                    STAGE -> sendCcpaConsentUrlProd(actionType = actionType.code)
                }
            }
            Legislation.GDPR -> {
                when (env) {
                    PROD -> HttpUrl.Builder()
                        .scheme("https")
                        .host("fake-wrapper-api.herokuapp.com")
                        .addPathSegments("all/v1/gdpr-consent")
                        .addQueryParameter("inApp", "true")
                        .addQueryParameter("env", "stage")
                        .build() // sendGdprConsentUrl
                    STAGE -> sendGdprConsentUrlProd(actionType = actionType.code)
                }
            }
        }
    }

    override fun pmUrl(env: Env, legislation: Legislation, pmConfig: PmUrlConfig?): HttpUrl = when (legislation) {
        Legislation.GDPR -> urlPmGdpr() // urlUWPm(pmConfig!!, UrlLegislation.valueOf(legislation.name))
        Legislation.CCPA -> urlPmCcpa() // urlUWPm(pmConfig!!, UrlLegislation.valueOf(legislation.name))
    }
}

enum class UrlLegislation(val segment: String) {
    GDPR("segment_gdpr"),
    CCPA("segment_ccpa")
}

enum class Env {
    STAGE,
    PROD
}
