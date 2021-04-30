package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.json.JSONObject

internal class MockService(
    private val getNativeMessageLogic: ((messageReq: UnifiedMessageRequest, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : Service {

    override fun getNativeMessage(messageReq: UnifiedMessageRequest, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {}
    override fun getNativeMessageK(messageReq: UnifiedMessageRequest, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {}
    override fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate) {}
    override fun isAppliedCampaign(campaignType: CampaignType): Boolean = false
    override fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>> = Left(RuntimeException())
    override fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate> = Left(RuntimeException())
    override fun getGDPRConsent(): Either<GDPRConsent> = Left(RuntimeException())
    override fun getCCPAConsent(): Either<CCPAConsent> = Left(RuntimeException())
    override fun saveGDPRConsent(consent: GDPRConsent?) {}
    override fun saveCCPAConsent(consent: CCPAConsent?) {}
    override fun parseRenderingMessage() {}
    override fun clearConsents() {}
    override fun getUnifiedMessageResp(): Either<UnifiedMessageResp> = Left(RuntimeException())
    override fun getGdpr(): Either<Gdpr> = Left(RuntimeException())
    override fun getCcpa(): Either<Ccpa> = Left(RuntimeException())
    override fun saveGdpr(gdpr: Gdpr) {}
    override fun saveCcpa(ccpa: Ccpa) {}
    override fun saveUnifiedMessageResp(unifiedMessageResp: UnifiedMessageResp) {}
    override fun getUnifiedMessage(messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit, env: Env) {}
    override fun getUnifiedMessageReq(): UnifiedMessageRequest {
        TODO("Not yet implemented")
    }

    override fun sendCustomConsentServ(customConsentReq: CustomConsentReq, env: Env): Either<SPConsents?> {
        TODO("Not yet implemented")
    }

    override fun sendCustomConsent(customConsentReq: CustomConsentReq, env: Env): Either<CustomConsentResp> {
        TODO("Not yet implemented")
    }

    override fun getPmConfig(campaignType: CampaignType, pmId: String?, pmTab: PMTab?): Either<PmUrlConfig> {
        TODO("Not yet implemented")
    }

    override fun sendConsent(consentReq: JSONObject, env: Env, consentAction: ConsentAction): Either<ConsentResp> {
        TODO("Not yet implemented")
    }

    override fun sendConsent(localState: String, consentAction: ConsentAction, env: Env, pmId: String?): Either<ConsentResp> {
        TODO("Not yet implemented")
    }

    override fun getUnifiedMessageReq(authId: String?): UnifiedMessageRequest {
        TODO("Not yet implemented")
    }

    override var spConfig: SpConfig
        get() = TODO("Not yet implemented")
        set(value) {}

    override val messageLanguage: MessageLanguage
        get() = TODO("Not yet implemented")
}
