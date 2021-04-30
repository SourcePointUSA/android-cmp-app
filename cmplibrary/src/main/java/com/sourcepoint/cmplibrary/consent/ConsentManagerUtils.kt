package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* //ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.flatMap
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.IncludeData
import com.sourcepoint.cmplibrary.model.LocalState
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.model.ext.toJsonObject
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.util.* // ktlint-disable

internal interface ConsentManagerUtils {

    fun buildConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject>
    fun buildGdprConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject>
    fun buildCcpaConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject>

    fun getGdprConsent(): Either<GDPRConsent>
    fun getCcpaConsent(): Either<CCPAConsent>
    fun hasGdprConsent(): Boolean
    fun hasCcpaConsent(): Boolean

    fun getSpConsent(): SPConsents?

    companion object
}

internal fun ConsentManagerUtils.Companion.create(
    campaignManager: CampaignManager,
    dataStorage: DataStorage,
    logger: Logger,
    uuid: String = UUID.randomUUID().toString()
): ConsentManagerUtils = ConsentManagerUtilsImpl(campaignManager, dataStorage, logger, uuid)

private class ConsentManagerUtilsImpl(
    val cm: CampaignManager,
    val ds: DataStorage,
    val logger: Logger,
    val uuid: String = UUID.randomUUID().toString()
) : ConsentManagerUtils {

    override fun buildConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject> {
        return when (action.campaignType) {
            CampaignType.GDPR -> buildGdprConsentReq(action, localState, pmId)
            CampaignType.CCPA -> buildCcpaConsentReq(action, localState, pmId)
        }
    }

    override fun buildGdprConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject> = check {
        logger.d(ConsentManagerUtilsImpl::class.java.name, "localState[$localState]")
        cm
            .getCampaignTemplate(CampaignType.GDPR)
            .flatMap { campaign -> cm.getGdpr().map { Pair(campaign, it) } }
            .map { pair ->
                val gdpr = pair.first
                JSONObject().apply {
                    put("propertyHref", cm.spConfig.propertyName)
                    put("accountId", cm.spConfig.accountId)
                    put("actionType", action.actionType.code)
                    put("choiceId", action.choiceId)
                    put("requestFromPM", action.requestFromPm)
                    put("privacyManagerId", pmId)
                    put("requestUUID", uuid)
                    put("pmSaveAndExitVariables", action.saveAndExitVariables)
                    put("localState", localState)
                    put("pubData", action.pubData)
                    put("consentLanguage", action.consentLanguage)
                    put("uuid", uuid)
                }
            }
            .executeOnLeft {
                fail("Error trying to build the gdpr body to send consents.", it)
            }
            .getOrNull() ?: fail("Error trying to build the gdpr body to send consents.")
    }

    override fun buildCcpaConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject> = check {
        logger.d(ConsentManagerUtilsImpl::class.java.name, "localState[$localState]")
        JSONObject().apply {
            put("accountId", cm.spConfig.accountId)
            put("privacyManagerId", pmId)
            put("localState", localState)
            put("pubData", action.pubData)
            put("requestUUID", uuid)
            put("pmSaveAndExitVariables", action.saveAndExitVariables)
            put("includeData", IncludeData(localState = LocalState("string")).toJsonObject())
        }
    }

    override fun getSpConsent(): SPConsents? {
        val gdprCached = getGdprConsent().getOrNull()
        val ccpaCached = getCcpaConsent().getOrNull()
        return SPConsents(
            gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
            ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) }
        )
    }

    override fun getGdprConsent(): Either<GDPRConsent> {
        return cm.getGDPRConsent()
    }

    override fun getCcpaConsent(): Either<CCPAConsent> {
        return cm.getCCPAConsent()
    }

    override fun hasGdprConsent(): Boolean = ds.getGdprConsentResp().isNotBlank()

    override fun hasCcpaConsent(): Boolean = ds.getGdprConsentResp().isNotBlank()
}
