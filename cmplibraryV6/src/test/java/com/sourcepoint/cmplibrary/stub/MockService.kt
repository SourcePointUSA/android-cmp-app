package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CampaignTemplate
import com.sourcepoint.cmplibrary.util.Either
import com.sourcepoint.cmplibrary.util.Either.*  // ktlint-disable
import org.json.JSONObject

internal class MockService(
    private val getMessageLogic: ((messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val getNativeMessageLogic: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : Service {

    override fun getMessage(messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) {}
    override fun getMessage1203(messageReq: MessageReq, pSuccess: (UnifiedMessageResp1203) -> Unit, pError: (Throwable) -> Unit) {}
    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) {}
    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) {}
    override fun sendConsent(legislation: Legislation, consentReq: JSONObject, success: (ConsentResp) -> Unit, error: (Throwable) -> Unit) {}
    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) {}
    override fun isAppliedCampaign(legislation: Legislation): Boolean = false
    override fun getGdpr(): Either<Gdpr> = Left(RuntimeException())
    override fun getCcpa(): Either<Ccpa> = Left(RuntimeException())
    override fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>> = Left(RuntimeException())
    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = Left(RuntimeException())
    override fun getGdprPmConfig(): Either<PmUrlConfig> = Left(RuntimeException())
    override fun getMessageReq(): MessageReq {
        TODO("Not yet implemented")
    }
    override fun getGDPRConsent(): Either<GDPRConsent> = Left(RuntimeException())
    override fun getCCPAConsent(): Either<CCPAConsent> = Left(RuntimeException())
    override fun saveGdpr(gdpr: Gdpr) {}
    override fun saveCcpa(ccpa: Ccpa) {}
    override fun saveGDPRConsent(consent: GDPRConsent?) {}
    override fun saveCCPAConsent(consent: CCPAConsent?) {}
    override fun parseRenderingMessage() {}
    override fun clearConsents() {}
    override fun getCcpaPmConfig(): Either<PmUrlConfig> = Left(RuntimeException())
    override fun getUnifiedMessageResp1203(): Either<UnifiedMessageResp1203> = Left(RuntimeException())
    override fun getGdpr1203(): Either<Gdpr1203> = Left(RuntimeException())
    override fun getCcpa1203(): Either<Ccpa1203> = Left(RuntimeException())
    override fun saveGdpr1203(gdpr: Gdpr1203) {}
    override fun saveCcpa1203(ccpa: Ccpa1203) {}
    override fun saveUnifiedMessageResp1203(unifiedMessageResp: UnifiedMessageResp1203) {}
}
