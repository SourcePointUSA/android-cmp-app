package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.converter.failParam
import com.sourcepoint.cmplibrary.data.network.model.CCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.ConsentAction
import com.sourcepoint.cmplibrary.data.network.model.GDPRConsent
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.util.* // ktlint-disable

internal interface ConsentManager {
    fun buildGdprConsentReq(action: ConsentAction): Either<JSONObject>
    fun saveGdprConsent(value: JSONObject)
    fun saveCcpaConsent(value: JSONObject)
    fun getGdprConsent(): Either<GDPRConsent>
    fun getCcpaConsent(): Either<CCPAConsent>
    fun hasGdprConsent(): Boolean
    fun hasCcpaConsent(): Boolean

    companion object
}

internal fun ConsentManager.Companion.create(
    campaignManager: CampaignManager,
    dataStorage: DataStorage,
    uuid: String = UUID.randomUUID().toString()
): ConsentManager = ConsentManagerImpl(campaignManager, dataStorage, uuid)

private class ConsentManagerImpl(
    val campaignManager: CampaignManager,
    val dataStorage: DataStorage,
    val uuid: String = UUID.randomUUID().toString()
) : ConsentManager {

    override fun buildGdprConsentReq(action: ConsentAction): Either<JSONObject> = check {
        campaignManager
            .getCampaignTemplate(Legislation.GDPR)
            .flatMap { campaign -> dataStorage.getGdpr().map { Pair(campaign, it) } }
            .map { pair ->
                val gdpr = pair.first
                val gdprConfig = pair.second

                JSONObject().apply {
                    put("pmSaveAndExitVariables", action.saveAndExitVariables)
                    put("requestFromPM", action.requestFromPm)
                    put("accountId", gdpr.accountId)
                    put("propertyId", gdpr.propertyId)
                    put("propertyHref", "https://${gdpr.propertyName}")
                    put("privacyManagerId", gdpr.pmId)
                    put("uuid", gdprConfig.uuid ?: failParam("gdprConfig.uuid"))
                    put("meta", gdprConfig.meta)
                    put("actionType", action.actionType.code)
                    put("choiceId", action.choiceId)
                    put("pubData", action.pubData)
                    put("requestUUID", uuid)
                    put("consentLanguage", action.consentLanguage ?: Locale.getDefault().language.toUpperCase(Locale.getDefault()))
                }
            }
            .executeOnLeft {
                fail("Error trying to build body to send consents.", it)
            }
            .getOrNull() ?: fail("Error trying to build body to send consents.")
    }

    override fun getGdprConsent(): Either<GDPRConsent> {
        return campaignManager.getGDPRConsent()
    }

    override fun getCcpaConsent(): Either<CCPAConsent> {
        return campaignManager.getCCPAConsent()
    }

    override fun saveGdprConsent(value: JSONObject) {
        dataStorage.saveGdprConsentResp(value.toString())
    }

    override fun saveCcpaConsent(value: JSONObject) {
        dataStorage.saveCcpaConsentResp(value.toString())
    }

    override fun hasGdprConsent(): Boolean = dataStorage.getGdprConsentResp().isNotBlank()

    override fun hasCcpaConsent(): Boolean = dataStorage.getGdprConsentResp().isNotBlank()
}
