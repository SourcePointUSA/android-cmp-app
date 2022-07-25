package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* //ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.flatMap
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.toJsonObject
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentActionImpl
import com.sourcepoint.cmplibrary.model.IncludeData
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.util.* // ktlint-disable

internal interface ConsentManagerUtils {

    fun buildConsentReq(actionImpl: ConsentActionImpl, localState: String, pmId: String?): Either<JSONObject>
    fun buildGdprConsentReq(actionImpl: ConsentActionImpl, localState: String, pmId: String?): Either<JSONObject>
    fun buildCcpaConsentReq(actionImpl: ConsentActionImpl, localState: String, pmId: String?): Either<JSONObject>

    fun getGdprConsent(): Either<GDPRConsentInternal>
    fun getCcpaConsent(): Either<CCPAConsentInternal>
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

    override fun buildConsentReq(actionImpl: ConsentActionImpl, localState: String, pmId: String?): Either<JSONObject> {
        return when (actionImpl.campaignType) {
            CampaignType.GDPR -> buildGdprConsentReq(actionImpl, localState, pmId)
            CampaignType.CCPA -> buildCcpaConsentReq(actionImpl, localState, pmId)
        }
    }

    override fun buildGdprConsentReq(actionImpl: ConsentActionImpl, localState: String, pmId: String?): Either<JSONObject> =
        check {
            cm
                .getCampaignTemplate(CampaignType.GDPR)
                .flatMap { campaign -> cm.getGdpr().map { Pair(campaign, it) } }
                .map { _ ->
                    JSONObject().apply {
                        put("propertyHref", cm.spConfig.propertyName)
                        put("accountId", cm.spConfig.accountId)
                        put("actionType", actionImpl.actionType.code)
                        put("choiceId", actionImpl.choiceId)
                        put("requestFromPM", actionImpl.requestFromPm)
                        put("privacyManagerId", pmId)
                        put("requestUUID", uuid)
                        put("pmSaveAndExitVariables", actionImpl.saveAndExitVariables)
                        put("localState", localState)
                        put("pubData", actionImpl.pubData)
                        put("consentLanguage", actionImpl.consentLanguage)
                        put("uuid", uuid)
                        put("includeData", IncludeData().toJsonObject())
                    }
                }
                .executeOnLeft {
                    fail("Error trying to build the gdpr body to send consents.", it)
                }
                .getOrNull() ?: fail("Error trying to build the gdpr body to send consents.")
        }

    override fun buildCcpaConsentReq(actionImpl: ConsentActionImpl, localState: String, pmId: String?): Either<JSONObject> = check {
        JSONObject().apply {
            put("accountId", cm.spConfig.accountId)
            put("privacyManagerId", pmId)
            put("localState", localState)
            put("pubData", actionImpl.pubData)
            put("requestUUID", uuid)
            put("pmSaveAndExitVariables", actionImpl.saveAndExitVariables)
            put("includeData", IncludeData().toJsonObject())
        }
    }

    override fun getSpConsent(): SPConsents {
        val gdprCached = getGdprConsent().getOrNull()
        val ccpaCached = getCcpaConsent().getOrNull()
        return SPConsents(
            gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
            ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) }
        )
    }

    override fun getGdprConsent(): Either<GDPRConsentInternal> {
        return cm.getGDPRConsent()
    }

    override fun getCcpaConsent(): Either<CCPAConsentInternal> {
        return cm.getCCPAConsent()
    }

    override fun hasGdprConsent(): Boolean = ds.getGdprConsentResp() != null

    override fun hasCcpaConsent(): Boolean = ds.getGdprConsentResp() != null
}
