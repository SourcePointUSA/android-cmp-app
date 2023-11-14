package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.toCCPAConsentInternal
import com.sourcepoint.cmplibrary.data.network.model.optimized.toGDPRUserConsent
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.* // ktlint-disable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.* // ktlint-disable

internal interface ConsentManagerUtils {

    fun hasGdprConsent(): Boolean
    fun hasCcpaConsent(): Boolean

    val gdprConsentOptimized: Either<GDPRConsentInternal>
    val ccpaConsentOptimized: Either<CCPAConsentInternal>

    fun updateGdprConsent(
        dataRecordedConsent: String,
        gdprConsentStatus: ConsentStatus,
        additionsChangeDate: String,
        legalBasisChangeDate: String
    ): ConsentStatus

    val shouldTriggerByGdprSample: Boolean
    val shouldTriggerByCcpaSample: Boolean
    val shouldTriggerByUsNatSample: Boolean

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

    override fun updateGdprConsent(
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

        val shouldReconsent = creationLessThanAdditions || creationLessThanLegalBasis

        val map = mapOf(
            "dataRecordedConsentDate" to "$dataRecordedConsentDate",
            "additionsChangeDateDate" to "$additionsChangeDateDate",
            "legalBasisChangeDateConsentDate" to "$legalBasisChangeDateConsentDate",
            "creationLessThanAdditions OR creationLessThanLegalBasis" to "$shouldReconsent",
        )

        logger.computation(
            tag = "Reconsent updateGdprConsent",
            msg = JSONObject(map).toString(),
            json = JSONObject(map)
        )

        return updatedCS
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

    override val shouldTriggerByUsNatSample: Boolean
        get() {
            return ds.usNatSamplingResult ?: kotlin.run {
                val sampling = (ds.usNatSamplingValue * 100).toInt()
                when {
                    sampling <= 0 -> {
                        ds.usNatSamplingResult = false
                        false
                    }
                    sampling >= 100 -> {
                        ds.usNatSamplingResult = true
                        true
                    }
                    else -> {
                        val num = (1 until 100).random()
                        val res = num in (1..sampling)
                        ds.usNatSamplingResult = res
                        res
                    }
                }
            }
        }
}
