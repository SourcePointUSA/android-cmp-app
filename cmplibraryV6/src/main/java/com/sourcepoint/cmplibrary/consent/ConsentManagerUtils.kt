package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* //ktlint-disable
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.executeOnLeft
import com.sourcepoint.cmplibrary.core.flatMap
import com.sourcepoint.cmplibrary.core.map
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.CCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.GDPRConsent
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.util.* // ktlint-disable

internal interface ConsentManagerUtils {
    fun buildConsentReq(action: ConsentAction, localState: String): Either<JSONObject>
    fun buildGdprConsentReq(action: ConsentAction, localState: String): Either<JSONObject>
    fun buildCcpaConsentReq(action: ConsentAction, localState: String): Either<JSONObject>

    fun buildConsentReq(action: ConsentAction): Either<JSONObject>
    fun buildGdprConsentReq(action: ConsentAction): Either<JSONObject>
    fun buildCcpaConsentReq(action: ConsentAction): Either<JSONObject>
    fun saveGdprConsent(value: JSONObject)
    fun saveCcpaConsent(value: JSONObject)
    fun getGdprConsent(): Either<GDPRConsent>
    fun getCcpaConsent(): Either<CCPAConsent>
    fun hasGdprConsent(): Boolean
    fun hasCcpaConsent(): Boolean

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

    override fun buildConsentReq(action: ConsentAction, localState: String): Either<JSONObject> {
        return when (action.legislation) {
            Legislation.GDPR -> buildGdprConsentReq(action, localState)
            Legislation.CCPA -> buildCcpaConsentReq(action, localState)
        }
    }

    override fun buildGdprConsentReq(action: ConsentAction, localState: String): Either<JSONObject> = check {
        logger.d(ConsentManagerUtilsImpl::class.java.name, "localState[$localState]")
        cm
            .getCampaignTemplate(Legislation.GDPR)
            .flatMap { campaign -> cm.getGdpr1203().map { Pair(campaign, it) } }
            .map { pair ->
                val gdpr = pair.first
                JSONObject().apply {
                    put("propertyHref", gdpr.propertyName)
                    put("accountId", gdpr.accountId)
                    put("actionType", action.actionType.code)
                    put("choiceId", action.choiceId)
                    put("requestFromPM", action.requestFromPm)
                    put("privacyManagerId", gdpr.pmId)
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

    override fun buildCcpaConsentReq(action: ConsentAction, localState: String): Either<JSONObject> = check {
        logger.d(ConsentManagerUtilsImpl::class.java.name, "localState[$localState]")
        cm
            .getCampaignTemplate(Legislation.CCPA)
            .flatMap { campaign -> cm.getCcpa1203().map { Pair(campaign, it) } }
            .map { pair ->
                val ccpa = pair.first
                val ccpaConfig = pair.second

                JSONObject().apply {
                    put("consents", ccpaConfig.userConsent.thisContent)
                    put("accountId", ccpa.accountId)
                    put("privacyManagerId", ccpa.pmId)
                    put("localState", localState)
                    put("pubData", action.pubData)
                    put("requestUUID", uuid)
                }
            }
            .executeOnLeft {
                fail("Error trying to build the ccpa body to send consents.", it)
            }
            .getOrNull() ?: fail("Error trying to build the ccpa body to send consents.")
    }

    override fun buildConsentReq(action: ConsentAction): Either<JSONObject> {
        return when (action.legislation) {
            Legislation.GDPR -> buildGdprConsentReq(action)
            Legislation.CCPA -> buildCcpaConsentReq(action)
        }
    }

    override fun buildGdprConsentReq(action: ConsentAction): Either<JSONObject> = check {
        val localState: String? = ds.getLocalState()
        cm
            .getCampaignTemplate(Legislation.GDPR)
            .flatMap { campaign -> cm.getGdpr1203().map { Pair(campaign, it) } }
            .map { pair ->
                val gdpr = pair.first
                val gdprConfig = pair.second

//                {
//                    "propertyHref": "https://tcfv2.mobile.webview",
//                    "accountId": 22,
//                    "actionType": 11,
//                    "choiceId": null,
//                    "requestFromPM": true,
//                    "privacyManagerId": "122058",
//                    "uuid": "",
//                    "requestUUID": "test",
//                    "pmSaveAndExitVariables": {},
//                    "meta": "{}",
//                    "pubData": "",
//                    "consentLanguage": "EN"
//                }

                JSONObject().apply {
                    put("propertyHref", "https://${gdpr.propertyName}")
                    put("accountId", gdpr.accountId)
                    put("actionType", action.actionType.code)
                    put("choiceId", action.choiceId)
                    put("requestFromPM", action.requestFromPm)
                    put("privacyManagerId", gdpr.pmId)
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

    override fun buildCcpaConsentReq(action: ConsentAction): Either<JSONObject> = check {
        val localState: String? = ds.getLocalState()
        cm
            .getCampaignTemplate(Legislation.CCPA)
            .flatMap { campaign -> cm.getCcpa1203().map { Pair(campaign, it) } }
            .map { pair ->
                val ccpa = pair.first
                val ccpaConfig = pair.second

                JSONObject().apply {
                    put("consents", ccpaConfig.userConsent.thisContent)
                    put("accountId", ccpa.accountId)
                    put("privacyManagerId", ccpa.pmId)
                    put("localState", localState)
                    put("pubData", action.pubData)
                    put("requestUUID", "asdfjhaDFJKl")
                }
            }
            .executeOnLeft {
                fail("Error trying to build the ccpa body to send consents.", it)
            }
            .getOrNull() ?: fail("Error trying to build the ccpa body to send consents.")
    }

    override fun getGdprConsent(): Either<GDPRConsent> {
        return cm.getGDPRConsent()
    }

    override fun getCcpaConsent(): Either<CCPAConsent> {
        return cm.getCCPAConsent()
    }

    override fun saveGdprConsent(value: JSONObject) {
        ds.saveGdprConsentResp(value.toString())
    }

    override fun saveCcpaConsent(value: JSONObject) {
        ds.saveCcpaConsentResp(value.toString())
    }

    override fun hasGdprConsent(): Boolean = ds.getGdprConsentResp().isNotBlank()

    override fun hasCcpaConsent(): Boolean = ds.getGdprConsentResp().isNotBlank()
}
