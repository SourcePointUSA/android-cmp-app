package com.sourcepoint.cmplibrary.data.network.model.optimized

import com.sourcepoint.cmplibrary.data.network.converter.GranularStateSerializer
import com.sourcepoint.cmplibrary.data.network.converter.SpConsentStatusSerializer
import com.sourcepoint.mobile_core.models.consents.ConsentStatus.ConsentStatusGranularStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.sourcepoint.mobile_core.models.consents.ConsentStatus as CoreConsentStatus
import com.sourcepoint.mobile_core.models.consents.GDPRConsent.GCMStatus as CoreGCMStatus

@Serializable
data class GoogleConsentMode(
    @SerialName("ad_storage") @Serializable(with = SpConsentStatusSerializer::class) val adStorage: GCMStatus?,
    @SerialName("analytics_storage") @Serializable(with = SpConsentStatusSerializer::class) val analyticsStorage: GCMStatus?,
    @SerialName("ad_user_data") @Serializable(with = SpConsentStatusSerializer::class) val adUserData: GCMStatus?,
    @SerialName("ad_personalization") @Serializable(with = SpConsentStatusSerializer::class) val adPersonalization: GCMStatus?,
) {
    constructor(core: CoreGCMStatus) : this(
        adStorage = GCMStatus.firstWithStatusOrNull(core.adStorage),
        analyticsStorage = GCMStatus.firstWithStatusOrNull(core.analyticsStorage),
        adUserData = GCMStatus.firstWithStatusOrNull(core.adUserData),
        adPersonalization = GCMStatus.firstWithStatusOrNull(core.adPersonalization),
    )
}

@Serializable
data class ConsentStatus(
    var consentedAll: Boolean?,
    val consentedToAny: Boolean?,
    val granularStatus: GranularStatus?,
    val hasConsentData: Boolean?,
    val rejectedAny: Boolean?,
    val rejectedLI: Boolean?,
    var legalBasisChanges: Boolean? = null,
    var vendorListAdditions: Boolean? = null
) {
    constructor(core: CoreConsentStatus) : this(
        consentedAll = core.consentedAll,
        consentedToAny = core.consentedToAny,
        hasConsentData = core.hasConsentData,
        rejectedAny = core.rejectedAny,
        rejectedLI = core.rejectedLI,
        legalBasisChanges = core.legalBasisChanges,
        vendorListAdditions = core.vendorListAdditions,
        granularStatus = core.granularStatus?.let { GranularStatus(it) },
    )

    @Serializable
    data class GranularStatus(
        @SerialName("defaultConsent") val defaultConsent: Boolean?,
        @SerialName("previousOptInAll") var previousOptInAll: Boolean?,
        @Serializable(with = GranularStateSerializer::class) val purposeConsent: GranularState?,
        @Serializable(with = GranularStateSerializer::class) val purposeLegInt: GranularState?,
        @Serializable(with = GranularStateSerializer::class) val vendorConsent: GranularState?,
        @Serializable(with = GranularStateSerializer::class) val vendorLegInt: GranularState?
    ) {
        constructor(core: ConsentStatusGranularStatus) : this(
            defaultConsent = core.defaultConsent,
            previousOptInAll = core.previousOptInAll,
            purposeConsent = GranularState.fromString(core.purposeConsent),
            purposeLegInt = GranularState.fromString(core.purposeLegInt),
            vendorConsent = GranularState.fromString(core.vendorConsent),
            vendorLegInt = GranularState.fromString(core.vendorLegInt),
        )
    }
}

@Serializable
data class USNatConsentStatus(
    val rejectedAny: Boolean?,
    var consentedToAll: Boolean?,
    val consentedToAny: Boolean?,
    val granularStatus: USNatGranularStatus?,
    val hasConsentData: Boolean?,
    var vendorListAdditions: Boolean? = null,
) {
    constructor(core: CoreConsentStatus) : this(
        rejectedAny = core.rejectedAny,
        consentedToAll = core.consentedToAll,
        consentedToAny = core.consentedToAny,
        hasConsentData = core.hasConsentData,
        vendorListAdditions = core.vendorListAdditions,
        granularStatus = core.granularStatus?.let { USNatGranularStatus(it) },
    )

    @Serializable
    data class USNatGranularStatus(
        val sellStatus: Boolean?,
        val shareStatus: Boolean?,
        val sensitiveDataStatus: Boolean?,
        val gpcStatus: Boolean?,
        val defaultConsent: Boolean?,
        var previousOptInAll: Boolean?,
        var purposeConsent: String?,
    ) {
        constructor(core: ConsentStatusGranularStatus) : this(
            sellStatus = core.sellStatus,
            shareStatus = core.shareStatus,
            sensitiveDataStatus = core.sensitiveDataStatus,
            gpcStatus = core.gpcStatus,
            defaultConsent = core.defaultConsent,
            previousOptInAll = core.previousOptInAll,
            purposeConsent = core.purposeConsent,
        )
    }
}
