package com.sourcepoint.cmplibrary.consent

import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.core.* // ktlint-disable
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.exception.InvalidConsentResponse
import com.sourcepoint.cmplibrary.model.exposed.* // ktlint-disable
import com.sourcepoint.cmplibrary.util.* // ktlint-disable

internal interface ConsentManagerUtils {
    val gdprConsentOptimized: Either<GDPRConsentInternal>
    val ccpaConsentOptimized: Either<CCPAConsentInternal>
    val usNatConsent: Either<UsNatConsentInternal>

    val spStoredConsent: Either<SPConsents>

    fun sample(samplingRate: Double): Boolean

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
        cm.usNatCS?.toUsNatConsentInternal() ?: throw InvalidConsentResponse(
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

    override fun sample(samplingRate: Double): Boolean =
        IntRange(1, 100).random() in (1..(samplingRate * 100).toInt())
}
