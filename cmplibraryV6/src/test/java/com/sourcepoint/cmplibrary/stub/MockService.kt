package com.sourcepoint.cmplibrary.stub

import com.sourcepoint.cmplibrary.data.Service
import com.sourcepoint.cmplibrary.data.network.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.consent.ConsentResp
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.CampaignTemplate
import com.sourcepoint.cmplibrary.util.Either
import org.json.JSONObject

internal class MockService(
    private val getMessageLogic: ((messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) -> Unit)? = null,
    private val getNativeMessageLogic: ((messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) -> Unit)? = null
) : Service {

    override fun getMessage(messageReq: MessageReq, pSuccess: (UnifiedMessageResp) -> Unit, pError: (Throwable) -> Unit) { }
    override fun getNativeMessage(messageReq: MessageReq, success: (NativeMessageResp) -> Unit, error: (Throwable) -> Unit) { }
    override fun getNativeMessageK(messageReq: MessageReq, success: (NativeMessageRespK) -> Unit, error: (Throwable) -> Unit) { }
    override fun sendConsent(legislation: Legislation, consentReq: JSONObject, success: (ConsentResp) -> Unit, error: (Throwable) -> Unit) { }
    override fun addCampaign(legislation: Legislation, campaign: CampaignTemplate) { }
    override fun isAppliedCampaign(legislation: Legislation): Boolean = false
    override fun getGdpr(): Either<Gdpr> = Either.Left(RuntimeException())
    override fun getCcpa(): Either<Ccpa> = Either.Left(RuntimeException())
    override fun getAppliedCampaign(): Either<Pair<Legislation, CampaignTemplate>> = Either.Left(RuntimeException())
    override fun getCampaignTemplate(legislation: Legislation): Either<CampaignTemplate> = Either.Left(RuntimeException())
    override fun getPmGDPRConfig(): Either<PmUrlConfig> = Either.Left(RuntimeException())
    override fun getMessageReq(): MessageReq {
        TODO("Not yet implemented")
    }
    override fun getGDPRConsent(): Either<GDPRConsent> = Either.Left(RuntimeException())
    override fun getCCPAConsent(): Either<CCPAConsent> = Either.Left(RuntimeException())
    override fun saveGdpr(gdpr: Gdpr) { }
    override fun saveCcpa(ccpa: Ccpa) { }
    override fun saveGDPRConsent(consent: GDPRConsent?) { }
    override fun saveCCPAConsent(consent: CCPAConsent?) { }
    override fun parseRenderingMessage() { }
    override fun clearConsents() { }
}
