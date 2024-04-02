package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.network.model.optimized.toCCPAConsentInternal
import com.sourcepoint.cmplibrary.data.network.model.optimized.toGDPRUserConsent
import com.sourcepoint.cmplibrary.data.network.model.optimized.toUsNatConsentInternal
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.* // ktlint-disable

internal interface ConsentManagerUtils {
    val gdprConsentOptimized: Either<GDPRConsentInternal>
    val ccpaConsentOptimized: Either<CCPAConsentInternal>
    val usNatConsent: Either<UsNatConsentInternal>

    val spStoredConsent: Either<SPConsents>

    val shouldTriggerByGdprSample: Boolean
    val shouldTriggerByCcpaSample: Boolean
    val shouldTriggerByUsNatSample: Boolean

    companion object {
        const val DEFAULT_SAMPLE_RATE: Double = 1.0
    }
}

internal fun ConsentManagerUtils.Companion.create(
    campaignManager: CampaignManager,
    dataStorage: DataStorage
): ConsentManagerUtils = ConsentManagerUtilsImpl(campaignManager, dataStorage)

private class ConsentManagerUtilsImpl(
    val cm: CampaignManager,
    val ds: DataStorage
) : ConsentManagerUtils {
    override val gdprConsentOptimized: Either<GDPRConsentInternal> get() = check {
        cm.gdprConsentStatus?.toGDPRUserConsent() ?: throw InvalidConsentResponse(
            cause = null,
            "The GDPR consent is null!!!"
        )
    }

    override val ccpaConsentOptimized: Either<CCPAConsentInternal> get() = check {
        cm.ccpaConsentStatus?.toCCPAConsentInternal() ?: throw InvalidConsentResponse(
            cause = null,
            "The CCPA consent is null!!!"
        )
    }
    override val usNatConsent: Either<UsNatConsentInternal> get() = check {
        cm.usNatConsentData?.toUsNatConsentInternal() ?: throw InvalidConsentResponse(
            cause = null,
            "The UsNat consent is null!!!"
        )
    }

    override val spStoredConsent: Either<SPConsents> get() = check {
        val ccpaCached = ccpaConsentOptimized.getOrNull()
        val usNatCached = usNatConsent.getOrNull()
        val gdprCached = gdprConsentOptimized.getOrNull()
        SPConsents(
            gdpr = gdprCached?.let { gc -> SPGDPRConsent(consent = gc) },
            ccpa = ccpaCached?.let { cc -> SPCCPAConsent(consent = cc) },
            usNat = usNatCached?.let { usNatConsent -> SpUsNatConsent(consent = usNatConsent) },
        )
    }

    override val shouldTriggerByGdprSample: Boolean get() = ds.gdprSamplingResult ?: run {
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

    override val shouldTriggerByCcpaSample: Boolean get() = ds.ccpaSamplingResult ?: run {
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

    override val shouldTriggerByUsNatSample: Boolean get() = ds.usNatSamplingResult ?: run {
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
