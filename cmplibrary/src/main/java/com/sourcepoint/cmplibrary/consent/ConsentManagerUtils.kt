package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.MessagesResp
import com.sourcepoint.cmplibrary.data.network.model.optimized.toCCPAConsentInternal
import com.sourcepoint.cmplibrary.data.network.model.optimized.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.toJsonObject
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.ConsentAction
import com.sourcepoint.cmplibrary.model.IncludeData
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

internal interface ConsentManagerUtils {

    fun buildConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject>
    fun buildGdprConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject>
    fun buildCcpaConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject>

    fun getGdprConsent(): Either<GDPRConsentInternal>
    fun getCcpaConsent(): Either<CCPAConsentInternal>
    fun hasGdprConsent(): Boolean
    fun hasCcpaConsent(): Boolean

    val gdprConsentOptimized: Either<GDPRConsentInternal>
    val ccpaConsentOptimized: Either<CCPAConsentInternal>

    fun getSpConsent(): SPConsents?

    fun updateGdprConsentOptimized(
        dataRecordedConsent: String,
        gdprConsentStatus: ConsentStatus,
        additionsChangeDate: String,
        legalBasisChangeDate: String
    ): ConsentStatus

    val shouldTriggerByGdprSample: Boolean
    val shouldTriggerByCcpaSample: Boolean
    var messagesResp: MessagesResp?

    companion object {
        const val DEFAULT_SAMPLE_RATE: Double = 1.0
    }
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

    override fun buildGdprConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject> =
        check {
            cm
                .getCampaignTemplate(CampaignType.GDPR)
                .flatMap { campaign -> cm.getGdpr().map { Pair(campaign, it) } }
                .map { _ ->
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
                        put("includeData", IncludeData().toJsonObject())
                    }
                }
                .executeOnLeft {
                    fail("Error trying to build the gdpr body to send consents.", it)
                }
                .getOrNull() ?: fail("Error trying to build the gdpr body to send consents.")
        }

    override fun buildCcpaConsentReq(action: ConsentAction, localState: String, pmId: String?): Either<JSONObject> =
        check {
            JSONObject().apply {
                put("accountId", cm.spConfig.accountId)
                put("privacyManagerId", pmId)
                put("localState", localState)
                put("pubData", action.pubData)
                put("requestUUID", uuid)
                put("pmSaveAndExitVariables", action.saveAndExitVariables)
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

    override fun updateGdprConsentOptimized(
        dataRecordedConsent: String,
        gdprConsentStatus: ConsentStatus,
        additionsChangeDate: String,
        legalBasisChangeDate: String
    ): ConsentStatus {

         val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        val dataRecordedConsentDate = formatter.parse(dataRecordedConsent)
        val additionsChangeDateDate = formatter.parse(additionsChangeDate)
        val legalBasisChangeDateConsentDate = formatter.parse(legalBasisChangeDate)

        val creationLessThanAdditions = dataRecordedConsentDate.before(additionsChangeDateDate)
        val creationLessThanLegalBasis = dataRecordedConsentDate.before(legalBasisChangeDateConsentDate)

        val updatedCS = gdprConsentStatus.copy()

        if (creationLessThanAdditions) {
            updatedCS.vendorListAdditions = true
        }
        if (creationLessThanLegalBasis) {
            updatedCS.legalBasisChanges = true
        }
        if (creationLessThanAdditions || creationLessThanLegalBasis) {
            if (updatedCS.consentedAll == true) {
                updatedCS.granularStatus?.previousOptInAll = true
                updatedCS.consentedAll = false
            }
        }

        return updatedCS
    }

    override fun getGdprConsent(): Either<GDPRConsentInternal> {
        return cm.getGDPRConsent()
    }

    override fun getCcpaConsent(): Either<CCPAConsentInternal> {
        return cm.getCCPAConsent()
    }

    override val gdprConsentOptimized: Either<GDPRConsentInternal>
        get() = check {
            cm.gdprConsentStatus?.toGDPRUserConsent() ?: throw InvalidConsentResponse(
                cause = null,
                "The GDPR consent is null!!!"
            )
        }

    override val ccpaConsentOptimized: Either<CCPAConsentInternal>
        get() = check {
            cm.ccpaConsentStatus?.toCCPAConsentInternal() ?: throw InvalidConsentResponse(
                cause = null,
                "The CCPA consent is null!!!"
            )
        }

    override fun hasGdprConsent(): Boolean = ds.getGdprConsentResp() != null

    override fun hasCcpaConsent(): Boolean = ds.getGdprConsentResp() != null

    override val shouldTriggerByGdprSample: Boolean
        get() {
            return ds.gdprSamplingResult ?: kotlin.run {
                val sampling = (ds.gdprSamplingValue * 100).toInt()
                when {
                    sampling <= 0 -> {
                        ds.gdprSamplingResult = false
                        false
                    }
                    sampling >= 100 -> {
                        ds.gdprSamplingResult = true
                        true
                    }
                    else -> {
                        val num = (1 until 100).random()
                        val res = num in (1..sampling)
                        ds.gdprSamplingResult = res
                        res
                    }
                }
            }
        }

    override val shouldTriggerByCcpaSample: Boolean
        get() {
            return ds.ccpaSamplingResult ?: kotlin.run {
                val sampling = (ds.ccpaSamplingValue * 100).toInt()
                when {
                    sampling <= 0 -> {
                        ds.ccpaSamplingResult = false
                        false
                    }
                    sampling >= 100 -> {
                        ds.ccpaSamplingResult = true
                        true
                    }
                    else -> {
                        val num = (1 until 100).random()
                        val res = num in (1..sampling)
                        ds.ccpaSamplingResult = res
                        res
                    }
                }
            }
        }

    override var messagesResp: MessagesResp?
        get() = TODO("Not yet implemented")
        set(value) {
            ds
        }
}
