package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.Either.Left
import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.CCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.GDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import org.json.JSONObject

internal class MockService(
    private val getNativeMessageLogic: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : Service {

    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {}
    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {}
    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) {}
    override fun isAppliedCampaign(legislation: Legislation): Boolean = false
    override fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>> = Left(RuntimeException())
    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = Left(RuntimeException())
    override fun getGdprPmConfig(pmId: String?, pmTab: PMTab): Either<PmUrlConfig> = Left(RuntimeException())
    override fun getMessageReq(): MessageReq {
        TODO("Not yet implemented")
    }
    override fun getGDPRConsent(): Either<GDPRConsent> = Left(RuntimeException())
    override fun getCCPAConsent(): Either<CCPAConsent> = Left(RuntimeException())
    override fun saveGDPRConsent(consent: GDPRConsent?) {}
    override fun saveCCPAConsent(consent: CCPAConsent?) {}
    override fun parseRenderingMessage() {}
    override fun clearConsents() {}
    override fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = Left(RuntimeException())
    override fun getUnifiedMessageResp1203(): Either<UnifiedMessageResp1203> = Left(RuntimeException())
    override fun getGdpr1203(): Either<Gdpr1203> = Left(RuntimeException())
    override fun getCcpa1203(): Either<Ccpa1203> = Left(RuntimeException())
    override fun saveGdpr1203(gdpr: Gdpr1203) {}
    override fun saveCcpa1203(ccpa: Ccpa1203) {}
    override fun saveUnifiedMessageResp1203(unifiedMessageResp: UnifiedMessageResp1203) {}
    override fun getUnifiedMessage(messageReq: UnifiedMessageRequest, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit, env: Env) {}
    override fun getUnifiedMessageReq(): UnifiedMessageRequest {
        TODO("Not yet implemented")
    }

    override fun sendConsent(consentReq: JSONObject, env: Env, consentAction: ConsentAction): Either<ConsentResp> {
        TODO("Not yet implemented")
    }

    override fun sendConsent(localState: String, consentAction: ConsentAction, env: Env, pmId: String?): Either<ConsentResp> {
        TODO("Not yet implemented")
    }

    override var spCampaignConfig: SpConfig
        get() = TODO("Not yet implemented")
        set(value) {}
}
