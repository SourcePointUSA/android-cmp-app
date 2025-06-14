package com.sourcepoint.cmplibrary.model.exposed

import com.sourcepoint.cmplibrary.data.network.model.optimized.ConsentStatus
import com.sourcepoint.cmplibrary.data.network.model.optimized.GoogleConsentMode
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentData
import com.sourcepoint.cmplibrary.data.network.model.optimized.USNatConsentStatus
import com.sourcepoint.mobile_core.models.consents.SPUserData
import com.sourcepoint.mobile_core.network.encodeToJsonObject
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonObject
import org.json.JSONObject
import com.sourcepoint.mobile_core.models.consents.CCPAConsent as CCPAConsentCore
import com.sourcepoint.mobile_core.models.consents.CCPAConsent.CCPAConsentStatus as CoreCCPAConsentStatus
import com.sourcepoint.mobile_core.models.consents.GDPRConsent as GDPRConsentCore
import com.sourcepoint.mobile_core.models.consents.PreferencesConsent as PreferencesConsentCore
import com.sourcepoint.mobile_core.models.consents.PreferencesConsent.PreferencesStatus as PreferencesStatusCore
import com.sourcepoint.mobile_core.models.consents.PreferencesConsent.PreferencesSubType as PreferencesSubTypeCore
import com.sourcepoint.mobile_core.models.consents.USNatConsent as USNATConsentCore

data class SPConsents(
    val gdpr: SPGDPRConsent? = null,
    val ccpa: SPCCPAConsent? = null,
    val usNat: SpUsNatConsent? = null,
    val preferences: SPPreferencesConsent? = null
) {
    internal constructor(core: SPUserData) : this(
        gdpr = core.gdpr?.consents?.let { SPGDPRConsent(consent = GDPRConsentInternal(it, core.gdpr?.childPmId)) },
        ccpa = core.ccpa?.consents?.let { SPCCPAConsent(consent = CCPAConsentInternal(it, core.ccpa?.childPmId)) },
        usNat = core.usnat?.consents?.let { SpUsNatConsent(consent = UsNatConsentInternal(it)) }, // SpUsNatConsent doesn't have childPmId
        preferences = core.preferences?.consents?.let { SPPreferencesConsent(consent = PreferencesConsentInternal(it)) } // SPPreferencesConsent doesn't have childPmId
    )
}

data class SPGDPRConsent(
    val consent: GDPRConsent
)

data class SPCCPAConsent(
    val consent: CCPAConsent
)

data class SpUsNatConsent(
    val consent: UsNatConsent,
)

data class SPPreferencesConsent(
    val consent: PreferencesConsent
)

@Serializable
data class GDPRPurposeGrants(
    @SerialName("vendorGrant") val granted: Boolean = false,
    val purposeGrants: Map<String, Boolean> = emptyMap()
)

interface GDPRConsent {
    val uuid: String?
    var euconsent: String
    var tcData: Map<String, Any?>
    var grants: Map<String, GDPRPurposeGrants>
    val acceptedCategories: List<String>?
    val applies: Boolean
    val webConsentPayload: JsonObject?
    val consentStatus: ConsentStatus?
    var googleConsentMode: GoogleConsentMode?
}

internal data class GDPRConsentInternal(
    override var euconsent: String = "",
    override val uuid: String? = null,
    override var tcData: Map<String, Any?> = emptyMap(),
    override var grants: Map<String, GDPRPurposeGrants> = emptyMap(),
    override val acceptedCategories: List<String>? = null,
    override val applies: Boolean = false,
    override val consentStatus: ConsentStatus? = null,
    override var googleConsentMode: GoogleConsentMode? = null,
    val childPmId: String? = null,
    val thisContent: JSONObject = JSONObject(),
    override val webConsentPayload: JsonObject? = null,
) : GDPRConsent {
    constructor(core: GDPRConsentCore, childPmId: String?) : this(
        euconsent = core.euconsent ?: "",
        uuid = core.uuid,
        tcData = core.tcData,
        grants = core.grants.entries.associate {
            it.key to GDPRPurposeGrants(granted = it.value.vendorGrant, purposeGrants = it.value.purposeGrants)
        },
        acceptedCategories = core.categories,
        applies = core.applies,
        consentStatus = ConsentStatus(core.consentStatus),
        googleConsentMode = core.gcmStatus?.let { GoogleConsentMode(it) },
        childPmId = childPmId,
        webConsentPayload = core.webConsentPayload?.encodeToJsonObject()
    )
}

interface CCPAConsent {
    val uuid: String?
    var gppData: Map<String, Any?>
    val rejectedCategories: List<String>
    val rejectedVendors: List<String>
    val status: CcpaStatus?
    val uspstring: String
    val childPmId: String?
    val applies: Boolean
    val signedLspa: Boolean?
    val webConsentPayload: JsonObject?
}

internal data class CCPAConsentInternal(
    override val uuid: String? = null,
    override var gppData: Map<String, Any?> = emptyMap(),
    override val rejectedCategories: List<String> = listOf(),
    override val rejectedVendors: List<String> = listOf(),
    override val status: CcpaStatus? = null,
    override val uspstring: String = "1YNN",
    override val childPmId: String? = null,
    override val applies: Boolean = false,
    val thisContent: JSONObject = JSONObject(),
    override val signedLspa: Boolean? = null,
    override val webConsentPayload: JsonObject? = null,
) : CCPAConsent {
    constructor(core: CCPAConsentCore, childPmId: String?) : this(
        uuid = core.uuid,
        gppData = core.gppData,
        rejectedCategories = core.rejectedCategories,
        rejectedVendors = core.rejectedVendors,
        status = core.status?.let { CcpaStatus.fromCore(it) },
        uspstring = core.uspstring,
        childPmId = childPmId,
        applies = core.applies,
        signedLspa = core.signedLspa,
        webConsentPayload = core.webConsentPayload?.encodeToJsonObject()
    )
}

enum class CcpaStatus {
    rejectedAll,
    rejectedSome,
    rejectedNone,
    consentedAll,
    linkedNoAction,
    unknown;

    companion object {
        fun fromCore(core: CoreCCPAConsentStatus) = when (core) {
            CoreCCPAConsentStatus.ConsentedAll -> consentedAll
            CoreCCPAConsentStatus.RejectedAll -> rejectedAll
            CoreCCPAConsentStatus.RejectedSome -> rejectedSome
            CoreCCPAConsentStatus.RejectedNone -> rejectedNone
            CoreCCPAConsentStatus.LinkedNoAction -> linkedNoAction
        }
    }

    fun toCore() = when (this) {
        consentedAll -> CoreCCPAConsentStatus.ConsentedAll
        rejectedAll -> CoreCCPAConsentStatus.RejectedAll
        rejectedSome -> CoreCCPAConsentStatus.RejectedSome
        rejectedNone -> CoreCCPAConsentStatus.RejectedNone
        linkedNoAction -> CoreCCPAConsentStatus.LinkedNoAction
        unknown -> null
    }
}

interface Consentable {
    val id: String
    val consented: Boolean
}
@Serializable
data class ConsentableImpl(
    @SerialName("_id") override val id: String,
    override val consented: Boolean
) : Consentable

data class UsNatStatuses(
    val hasConsentData: Boolean?,
    val rejectedAny: Boolean?,
    val consentedToAll: Boolean?,
    val consentedToAny: Boolean?,
    val sellStatus: Boolean?,
    val shareStatus: Boolean?,
    val sensitiveDataStatus: Boolean?,
    val gpcStatus: Boolean?,
)

interface UsNatConsent {
    var gppData: Map<String, Any?>
    val applies: Boolean

    @Deprecated("`consentStatus` is deprecated and will be renamed to `statuses` in the next release.", ReplaceWith("statuses"))
    val consentStatus: USNatConsentStatus?
    val statuses: UsNatStatuses
    val consentStrings: List<USNatConsentData.ConsentString>?
    val dateCreated: String?
    val vendors: List<Consentable>?
    val categories: List<Consentable>?
    val uuid: String?
    val webConsentPayload: JsonObject?
}

internal data class UsNatConsentInternal(
    override var gppData: Map<String, Any?> = emptyMap(),
    override val applies: Boolean = false,
    override val consentStrings: List<USNatConsentData.ConsentString> = emptyList(),
    override val dateCreated: String? = null,
    override val vendors: List<ConsentableImpl> = listOf(),
    override val categories: List<ConsentableImpl> = listOf(),
    override val uuid: String? = null,
    override val webConsentPayload: JsonObject? = null,
    @Deprecated("`consentStatus` is deprecated and will be renamed to `statuses` in the next release.", ReplaceWith("statuses"))
    override val consentStatus: USNatConsentStatus? = null,
    val url: String? = null,
) : UsNatConsent {
    override val statuses: UsNatStatuses
        get() = UsNatStatuses(
            hasConsentData = consentStatus?.hasConsentData,
            rejectedAny = consentStatus?.rejectedAny,
            consentedToAll = consentStatus?.consentedToAll,
            consentedToAny = consentStatus?.consentedToAny,
            sellStatus = consentStatus?.granularStatus?.sellStatus,
            shareStatus = consentStatus?.granularStatus?.shareStatus,
            sensitiveDataStatus = consentStatus?.granularStatus?.sensitiveDataStatus,
            gpcStatus = consentStatus?.granularStatus?.gpcStatus,
        )

    constructor(core: USNATConsentCore) : this(
        gppData = core.gppData,
        applies = core.applies,
        consentStrings = core.consentStrings.map {
            USNatConsentData.ConsentString(
                sectionId = it.sectionId,
                sectionName = it.sectionName,
                consentString = it.consentString
            )
        },
        dateCreated = core.dateCreated.toString(),
        vendors = core.userConsents.vendors.map { ConsentableImpl(id = it.id, consented = it.consented) },
        categories = core.userConsents.categories.map { ConsentableImpl(id = it.id, consented = it.consented) },
        uuid = core.uuid,
        webConsentPayload = core.webConsentPayload?.encodeToJsonObject(),
        consentStatus = USNatConsentStatus(core.consentStatus)
    )
}

interface PreferencesConsent {
    val dateCreated: Instant?
    val messageId: Int?
    val status: List<Status>?
    val rejectedStatus: List<Status>?
    val uuid: String?

    interface Status {
        val categoryId: Int
        val channels: List<Channel>?
        val changed: Boolean?
        val dateConsented: Instant?
        val subType: SubType?
    }

    interface Channel {
        val id: Int
        val status: Boolean
    }

    enum class SubType {
        Unknown,
        AIPolicy,
        TermsAndConditions,
        PrivacyPolicy,
        LegalPolicy,
        TermsOfSale;

        companion object {
            fun fromCore(subType: PreferencesSubTypeCore?) = when (subType) {
                PreferencesSubTypeCore.Unknown -> Unknown
                PreferencesSubTypeCore.AIPolicy -> AIPolicy
                PreferencesSubTypeCore.TermsAndConditions -> TermsAndConditions
                PreferencesSubTypeCore.PrivacyPolicy -> PrivacyPolicy
                PreferencesSubTypeCore.LegalPolicy -> LegalPolicy
                PreferencesSubTypeCore.TermsOfSale -> TermsOfSale
                null -> Unknown
            }
        }
    }
}

internal data class PreferencesConsentInternal(
    override val dateCreated: Instant? = null,
    override val messageId: Int? = null,
    override val status: List<PreferencesConsent.Status>? = emptyList(),
    override val rejectedStatus: List<PreferencesConsent.Status>? = emptyList(),
    override val uuid: String? = null
) : PreferencesConsent {
    constructor(core: PreferencesConsentCore) : this(
        dateCreated = core.dateCreated,
        messageId = core.messageId,
        status = core.status?.map { Status(it) },
        rejectedStatus = core.rejectedStatus?.map { Status(it) },
        uuid = core.uuid
    )

    data class Status(
        override val categoryId: Int,
        override val channels: List<Channel>?,
        override val changed: Boolean?,
        override val dateConsented: Instant?,
        override val subType: PreferencesConsent.SubType? = PreferencesConsent.SubType.Unknown
    ): PreferencesConsent.Status {
        constructor(status: PreferencesStatusCore) : this(
            categoryId = status.categoryId,
            channels = status.channels?.map { Channel(id = it.channelId, status = it.status) },
            changed = status.changed,
            dateConsented = status.dateConsented,
            subType = PreferencesConsent.SubType.fromCore(status.subType)
        )

        data class Channel(
            override val id: Int,
            override val status: Boolean
        ): PreferencesConsent.Channel
    }
}

fun SPConsents.toWebViewConsentsJsonObject(): JsonObject = buildJsonObject {
    ccpa?.consent?.let { ccpaConsent ->
        if (ccpaConsent.isWebConsentEligible()) {
            putJsonObject("ccpa") {
                put("uuid", JsonPrimitive(ccpaConsent.uuid))
                put("webConsentPayload", JsonPrimitive(ccpaConsent.webConsentPayload.toString()))
            }
        }
    }
    gdpr?.consent?.let { gdprConsent ->
        if (gdprConsent.isWebConsentEligible()) {
            putJsonObject("gdpr") {
                put("uuid", JsonPrimitive(gdprConsent.uuid))
                put("webConsentPayload", JsonPrimitive(gdprConsent.webConsentPayload.toString()))
            }
        }
    }
    usNat?.consent?.let { usNatConsent ->
        if (usNatConsent.isWebConsentEligible()) {
            putJsonObject("usnat") {
                put("uuid", JsonPrimitive(usNatConsent.uuid))
                put("webConsentPayload", JsonPrimitive(usNatConsent.webConsentPayload.toString()))
            }
        }
    }
}

internal fun GDPRConsent.isWebConsentEligible() =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

internal fun CCPAConsent.isWebConsentEligible() =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()

internal fun UsNatConsent.isWebConsentEligible() =
    uuid.isNullOrEmpty().not() && webConsentPayload.isNullOrEmpty().not()
