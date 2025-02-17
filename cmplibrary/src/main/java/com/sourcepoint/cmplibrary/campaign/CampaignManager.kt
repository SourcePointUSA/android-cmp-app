package com.sourcepoint.cmplibrary.campaign

import com.example.cmplibrary.BuildConfig
import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.SIMPLE_DATE_FORMAT_PATTERN
import com.sourcepoint.cmplibrary.campaign.CampaignManager.Companion.selectPmId
import com.sourcepoint.cmplibrary.core.Either
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.creation.validPattern
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE
import com.sourcepoint.cmplibrary.data.local.DataStorage.Companion.LOCAL_STATE_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_APPLIES_OLD
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr.Companion.KEY_GDPR_MESSAGE_SUBCATEGORY_OLD
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.converter.fail
import com.sourcepoint.cmplibrary.data.network.model.optimized.* //ktlint-disable
import com.sourcepoint.cmplibrary.data.network.model.optimized.choice.ChoiceResp
import com.sourcepoint.cmplibrary.data.network.util.Env
import com.sourcepoint.cmplibrary.exception.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType.CCPA
import com.sourcepoint.cmplibrary.exception.CampaignType.GDPR
import com.sourcepoint.cmplibrary.exception.CampaignType.USNAT
import com.sourcepoint.cmplibrary.model.* //ktlint-disable
import com.sourcepoint.cmplibrary.model.exposed.* //ktlint-disable
import com.sourcepoint.cmplibrary.util.check
import com.sourcepoint.cmplibrary.util.extensions.isIncluded
import com.sourcepoint.cmplibrary.util.updateCcpaUspString
import com.sourcepoint.mobile_core.network.requests.PvDataRequest
import com.sourcepoint.mobile_core.network.responses.MetaDataResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.* //ktlint-disable
import kotlin.math.abs

internal interface CampaignManager {
    val spConfig: SpConfig
    val messageLanguage: MessageLanguage
    val campaigns4Config: List<CampaignReq>
    val ccpaMessageSubCategory: MessageSubCategory
    val gdprMessageSubCategory: MessageSubCategory
    fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate)

    fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>>
    fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate>

    fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig>

    fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?
    ): Either<PmUrlConfig>

    fun getMessageOptimizedReq(authId: String?, pubData: JSONObject?): MessagesParamReq

    fun getGroupId(campaignType: CampaignType): String?

    fun clearConsents()

    // Optimized
    val shouldCallMessages: Boolean
    fun shouldCallConsentStatus(authId: String?): Boolean
    var gdprMessageMetaData: MessageMetaData?
    var ccpaMessageMetaData: MessageMetaData?

    // Consent Status
    var gdprConsentStatus: GdprCS?
    var ccpaConsentStatus: CcpaCS?
    var usNatConsentData: USNatConsentData?
    var messagesOptimizedLocalState: String?
    var nonKeyedLocalState: JsonElement?
    var gdprUuid: String?
    var ccpaUuid: String?
    val hasLocalData: Boolean
    val isGdprExpired: Boolean
    val isCcpaExpired: Boolean
    val isUsnatExpired: Boolean
    val storeChoiceResp: ChoiceResp

    // dateCreated
    var gdprDateCreated: String?
    var ccpaDateCreated: String?

    var metaDataResp: MetaDataResponse?
    var choiceResp: ChoiceResp?
    var dataRecordedConsent: Instant?
    var authId: String?
    var propertyId: Int

    // reconsent
    val gdprLegalBasisChangeDate: String?
    val gdprAdditionsChangeDate: String?
    val usnatAdditionsChangeDate: String?

    fun handleAuthIdOrPropertyIdChange(newAuthId: String?, newPropertyId: Int)
    fun handleMetaDataResponse(response: MetaDataResponse?)
    fun handleOldLocalData()
    fun getGdprPvDataBody(messageReq: MessagesParamReq): PvDataRequest.GDPR
    fun getCcpaPvDataBody(messageReq: MessagesParamReq): PvDataRequest.CCPA
    fun getUsNatPvDataBody(messageReq: MessagesParamReq): PvDataRequest.USNat
    fun deleteExpiredConsents()
    fun hasGdprVendorListIdChanged(gdprVendorListId: String?): Boolean
    fun hasUsNatVendorListIdChanged(usNatVendorListId: String?): Boolean
    fun reConsentGdpr(additionsChangeDate: String?, legalBasisChangeDate: String?): ConsentStatus?
    fun reConsentUsnat(additionsChangeDate: String?): USNatConsentStatus?
    fun hasUsnatApplicableSectionsChanged(usnatMetaData: MetaDataResponse.MetaDataResponseUSNat?): Boolean

    companion object {
        const val SIMPLE_DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        fun selectPmId(userPmId: String?, childPmId: String?, useGroupPmIfAvailable: Boolean): String {
            return when {
                useGroupPmIfAvailable && !childPmId.isNullOrEmpty() && childPmId.isNotBlank() -> childPmId
                else -> userPmId ?: ""
            }
        }
    }
}

internal fun CampaignManager.Companion.create(
    dataStorage: DataStorage,
    spConfig: SpConfig
): CampaignManager = CampaignManagerImpl(dataStorage, spConfig)

private class CampaignManagerImpl(
    val dataStorage: DataStorage,
    override val spConfig: SpConfig
) : CampaignManager {

    override val messageLanguage: MessageLanguage = spConfig.messageLanguage
    val formatter by lazy { SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN, Locale.getDefault()) }

    private val mapTemplate = mutableMapOf<String, CampaignTemplate>()
    val logger: Logger? = spConfig.logger
    var usnatApplicableSectionChanged: Boolean = false

    init {
        if (!spConfig.propertyName.contains(validPattern)) {
            throw InvalidArgumentException(
                description = """
                PropertyName can only include letters, numbers, '.', ':', '-' and '/'. (string) passed is invalid
                """.trimIndent()
            )
        }
        spConfig.also { spp ->
            spp.campaigns.forEach {
                when (it.campaignType) {
                    GDPR -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }

                    USNAT -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }

                    CCPA -> {
                        addCampaign(
                            it.campaignType,
                            CampaignTemplate(
                                spp.campaignsEnv,
                                it.targetingParams.filter { tp -> tp.key != "campaignEnv" },
                                it.campaignType,
                                it.groupPmId
                            )
                        )
                    }
                }
            }
        }
    }

    override val campaigns4Config: List<CampaignReq>
        get() {
            val campaigns = mutableListOf<CampaignReq>()
            mapTemplate[GDPR.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv,
                        groupPmId = it.groupPmId
                    )
                }
                ?.let { campaigns.add(it) }
            mapTemplate[CCPA.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv
                    )
                }
                ?.let { campaigns.add(it) }
            mapTemplate[USNAT.name]
                ?.let {
                    it.toCampaignReqImpl(
                        targetingParams = it.targetingParams,
                        campaignsEnv = it.campaignsEnv
                    )
                }
                ?.let { campaigns.add(it) }
            return campaigns
        }

    override val ccpaMessageSubCategory: MessageSubCategory
        get() = ccpaMessageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2

    override val gdprMessageSubCategory: MessageSubCategory
        get() = gdprMessageMetaData?.subCategoryId ?: MessageSubCategory.TCFv2

    override fun addCampaign(campaignType: CampaignType, campaign: CampaignTemplate) {
        mapTemplate[campaignType.name] = campaign
    }

    override fun getCampaignTemplate(campaignType: CampaignType): Either<CampaignTemplate> = check {
        mapTemplate[campaignType.name] ?: fail("${campaignType.name} Campaign is missing!!!")
    }

    override fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig> {
        return when (campaignType) {
            GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES, useGroupPmIfAvailable, groupPmId)
            CCPA -> getCcpaPmConfig(pmId)
            USNAT -> getUsNatPmConfig(pmId, groupPmId)
        }
    }

    override fun getPmConfig(
        campaignType: CampaignType,
        pmId: String?,
        pmTab: PMTab?
    ): Either<PmUrlConfig> {
        return when (campaignType) {
            GDPR -> getGdprPmConfig(pmId, pmTab ?: PMTab.PURPOSES, false, null)
            CCPA -> getCcpaPmConfig(pmId)
            USNAT -> getUsNatPmConfig(pmId)
        }
    }

    private fun getGdprPmConfig(
        pmId: String?,
        pmTab: PMTab,
        useGroupPmIfAvailable: Boolean,
        groupPmId: String?
    ): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.gdprChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId: Boolean = groupPmId != null

        val usedPmId = selectPmId(pmId, childPmId, useGroupPmIfAvailable)

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
            logger?.e(
                tag = "The childPmId is missing",
                msg = """
                              childPmId [null]
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                """.trimIndent()
            )
        }

        logger?.computation(
            tag = "Property group - GDPR PM",
            msg = """
                pmId[$pmId]
                childPmId[$childPmId]
                useGroupPmIfAvailable [$useGroupPmIfAvailable] 
                Query Parameter pmId[$usedPmId]
            """.trimIndent()
        )

        PmUrlConfig(
            pmTab = pmTab,
            consentLanguage = spConfig.messageLanguage.value,
            uuid = gdprUuid,
            siteId = spConfig.propertyId.toString(),
            messageId = usedPmId
        )
    }

    private fun getCcpaPmConfig(pmId: String?): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.ccpaChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId = false // feature not yet implemented
        val useGroupPmIfAvailable = false // feature not yet implemented

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
        }

        val usedPmId = childPmId ?: pmId

        PmUrlConfig(
            consentLanguage = spConfig.messageLanguage.value,
            uuid = ccpaUuid,
            siteId = spConfig.propertyId.toString(),
            messageId = usedPmId
        )
    }

    private fun getUsNatPmConfig(
        pmId: String?,
        groupPmId: String? = null,
        useGroupPmIfAvailable: Boolean = false
    ): Either<PmUrlConfig> = check {
        val childPmId: String? = dataStorage.usnatChildPmId
        val isChildPmIdAbsent: Boolean = childPmId == null
        val hasGroupPmId: Boolean = groupPmId != null

        val usedPmId = selectPmId(pmId, childPmId, useGroupPmIfAvailable)

        if (hasGroupPmId && useGroupPmIfAvailable && isChildPmIdAbsent) {
            logger?.error(
                ChildPmIdNotFound(
                    description = """
                              childPmId not found!!!
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                    """.trimIndent()
                )
            )
            logger?.e(
                tag = "The childPmId is missing",
                msg = """
                              childPmId [null]
                              GroupPmId[$groupPmId]
                              useGroupPmIfAvailable [true] 
                """.trimIndent()
            )
        }

        logger?.computation(
            tag = "Property group - USNAT PM",
            msg = """
                pmId[$pmId]
                childPmId[$childPmId]
                useGroupPmIfAvailable [$useGroupPmIfAvailable] 
                Query Parameter pmId[$usedPmId]
            """.trimIndent()
        )

        PmUrlConfig(
            consentLanguage = spConfig.messageLanguage.value,
            uuid = usNatConsentData?.uuid,
            siteId = spConfig.propertyId.toString(),
            messageId = pmId
        )
    }

    override fun getAppliedCampaign(): Either<Pair<CampaignType, CampaignTemplate>> = check {
        when {
            dataStorage
                .getGdprMessage().isNotBlank() -> Pair(GDPR, mapTemplate[GDPR.name]!!)
            dataStorage
                .getCcpaMessage().isNotBlank() -> Pair(CCPA, mapTemplate[CCPA.name]!!)
            else -> throw MissingPropertyException(description = "Inconsistent Legislation!!!")
        }
    }

    override fun getMessageOptimizedReq(authId: String?, pubData: JSONObject?): MessagesParamReq {
        val campaigns = mutableListOf<CampaignReq>()
        mapTemplate[GDPR.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv,
                    groupPmId = it.groupPmId
                )
            }
            ?.let { campaigns.add(it) }
        mapTemplate[CCPA.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv
                )
            }
            ?.let { campaigns.add(it) }
        mapTemplate[USNAT.name]
            ?.let {
                it.toCampaignReqImpl(
                    targetingParams = it.targetingParams,
                    campaignsEnv = it.campaignsEnv
                )
            }
            ?.let { campaigns.add(it) }

        return MessagesParamReq(
            metadataArg = campaigns.toMetadataArgs(),
            body = "",
            env = Env.values().find { it.name == BuildConfig.SDK_ENV } ?: Env.PROD,
            propertyHref = spConfig.propertyName,
            accountId = spConfig.accountId.toLong(),
            authId = authId,
            propertyId = spConfig.propertyId.toLong(),
            pubData = pubData?.toString()
                ?.let { check { JsonConverter.converter.decodeFromString<JsonObject>(it) }.getOrNull() }
                ?: JsonObject(mapOf())
        )
    }

    override fun getGroupId(campaignType: CampaignType): String? {
        return spConfig.campaigns.find { it.campaignType == campaignType }?.groupPmId
    }

    override fun clearConsents() {
        dataStorage.clearGdprConsent()
        dataStorage.clearCcpaConsent()
    }

    override var authId: String?
        get() = dataStorage.getAuthId()
        set(value) {
            dataStorage.saveAuthId(value)
        }

    override var propertyId: Int
        get() = dataStorage.propertyId
        set(value) {
            dataStorage.propertyId = value
        }

    // Optimized Implementation below

    val isNewUser: Boolean
        get() {
            val localStateSize = messagesOptimizedLocalState?.length ?: 0
            return messagesOptimizedLocalState == null ||
                localStateSize == 0 ||
                (gdprUuid == null && usNatConsentData?.uuid == null && (ccpaConsentStatus?.newUser == null || ccpaConsentStatus?.newUser == true))
        }

    override val shouldCallMessages: Boolean
        get() {

            val gdprToBeCompleted: Boolean = spConfig.isIncluded(GDPR) && (gdprConsentStatus?.consentStatus?.consentedAll != true)

            val ccpaToBeCompleted: Boolean = spConfig.isIncluded(CCPA)

            val usNatToBeCompleted: Boolean = spConfig.isIncluded(USNAT)

            val res = (isNewUser || ccpaToBeCompleted || gdprToBeCompleted || usNatToBeCompleted)

            logger?.computation(
                tag = "shouldCallMessages",
                msg = """
                isNewUser[$isNewUser]
                ccpaToBeCompleted[$ccpaToBeCompleted]
                gdprToBeCompleted[$gdprToBeCompleted]
                usNatToBeCompleted[$usNatToBeCompleted]
                shouldCallMessages[$res]  
                """.trimIndent()
            )

            return res
        }

    override fun shouldCallConsentStatus(authId: String?): Boolean {
        val isGdprUuidPresent = dataStorage.gdprConsentUuid != null
        val isCcpaUuidPresent = dataStorage.ccpaConsentUuid != null
        val isUsNatUuidPresent = usNatConsentData?.uuid != null
        val isLocalStateEmpty = messagesOptimizedLocalState == null
        val isV630LocalStatePresent = dataStorage.preference.all.containsKey(LOCAL_STATE)
        val isV690LocalStatePresent = dataStorage.preference.all.containsKey(LOCAL_STATE_OLD)
        val ccpa2usnat = (
            ccpaConsentStatus != null &&
                usNatConsentData == null &&
                spConfig.isIncluded(USNAT)
            )

        val storedCcpaWithoutGPP = ccpaConsentStatus?.let { it.gppData.isNullOrEmpty() } ?: false

        return ((isGdprUuidPresent || isCcpaUuidPresent || isUsNatUuidPresent) && isLocalStateEmpty) ||
            isV630LocalStatePresent ||
            isV690LocalStatePresent ||
            storedCcpaWithoutGPP ||
            usnatApplicableSectionChanged ||
            ccpa2usnat ||
            authId != null
    }

    override var gdprMessageMetaData: MessageMetaData?
        get() {
            return dataStorage.gdprMessageMetaData?.let { JsonConverter.converter.decodeFromString<MessageMetaData>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.gdprMessageMetaData = serialised
        }

    override var ccpaMessageMetaData: MessageMetaData?
        get() {
            return dataStorage.ccpaMessageMetaData?.let { JsonConverter.converter.decodeFromString<MessageMetaData>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.ccpaMessageMetaData = serialised
        }

    override var gdprConsentStatus: GdprCS?
        get() {
            return dataStorage.gdprConsentStatus
                ?.let { JsonConverter.converter.decodeFromString<GdprCS>(it) }
                ?.copy(applies = dataStorage.gdprApplies)
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.apply {
                gdprConsentStatus = serialised
                value?.TCData
                    ?.let { this.tcData = it }
                    ?: run { clearTCData() }
            }
        }

    override var ccpaConsentStatus: CcpaCS?
        get() {
            return dataStorage.ccpaConsentStatus
                ?.let { JsonConverter.converter.decodeFromString<CcpaCS>(it) }
                ?.copy(applies = metaDataResp?.ccpa?.applies)
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.run {
                ccpaConsentStatus = serialised
                gppData = value?.gppData
                uspstring = value?.uspstring
            }
        }

    override var usNatConsentData: USNatConsentData?
        get() {
            return dataStorage.usNatConsentData
                ?.let { JsonConverter.converter.decodeFromString<USNatConsentData>(it) }
                ?.copy(applies = metaDataResp?.usnat?.applies)
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.run {
                usNatConsentData = serialised
                gppData = value?.gppData
            }
        }

    override var messagesOptimizedLocalState: String?
        get() = dataStorage.messagesOptimizedLocalState
        set(value) {
            dataStorage.messagesOptimizedLocalState = value
        }

    override var nonKeyedLocalState: JsonElement?
        get() {
            return dataStorage.nonKeyedLocalState?.let {
                JsonConverter.converter.decodeFromString<JsonElement>(
                    it
                )
            }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.nonKeyedLocalState = serialised
        }

    override var gdprUuid: String?
        get() {
            return dataStorage.gdprConsentUuid
        }
        set(value) {
            dataStorage.gdprConsentUuid = value
        }

    override var ccpaUuid: String?
        get() {
            return dataStorage.ccpaConsentUuid
        }
        set(value) {
            dataStorage.ccpaConsentUuid = value
        }

    override val hasLocalData: Boolean
        get() = dataStorage.gdprConsentStatus != null || dataStorage.ccpaConsentStatus != null

    override val gdprAdditionsChangeDate: String?
        get() = metaDataResp?.gdpr?.additionsChangeDate

    override val gdprLegalBasisChangeDate: String?
        get() = metaDataResp?.gdpr?.legalBasisChangeDate

    override val usnatAdditionsChangeDate: String?
        get() = metaDataResp?.usnat?.additionsChangeDate

    override fun handleAuthIdOrPropertyIdChange(
        newAuthId: String?,
        newPropertyId: Int,
    ) {
        val isNewAuthId = newAuthId != null && newAuthId != authId && authId != null
        val isNewPropertyId = newPropertyId != propertyId
        val hasPreviousPropertyId = propertyId != 0
        if (isNewAuthId || isNewPropertyId && hasPreviousPropertyId) {
            dataStorage.clearAll()
        }

        logger?.computation(
            tag = "flush local data",
            msg = """
                isNewAuthId[$isNewAuthId] 
                isNewPropertyId && hasPreviousPropertyId[${isNewPropertyId && hasPreviousPropertyId}]
                return [${isNewAuthId || isNewPropertyId && hasPreviousPropertyId}]
            """.trimIndent()
        )

        authId = newAuthId
        propertyId = newPropertyId
    }

    override fun handleMetaDataResponse(response: MetaDataResponse?) {
        if (hasGdprVendorListIdChanged(gdprVendorListId = response?.gdpr?.vendorListId)) {
            dataStorage.deleteGdprConsent()
        }

        if (hasUsNatVendorListIdChanged(usNatVendorListId = response?.usnat?.vendorListId)) {
            dataStorage.deleteUsNatConsent()
        }

        usnatApplicableSectionChanged = hasUsnatApplicableSectionsChanged(response?.usnat)

        metaDataResp = response

        if (response == null) return

        response.ccpa?.let { ccpa ->
            ccpaConsentStatus = ccpaConsentStatus?.copy(
                applies = ccpa.applies,
                uspstring = updateCcpaUspString(ccpaConsentStatus, logger)
            )

            ccpa.sampleRate.let { newRate ->
                if (!newRate.toDouble().almostSameAs(dataStorage.ccpaSampleRate)) {
                    dataStorage.ccpaSampleRate = newRate.toDouble()
                    dataStorage.ccpaSampled = null
                }
            }
        }

        response.gdpr?.let { gdpr ->
            gdprConsentStatus = gdprConsentStatus?.copy(applies = gdpr.applies)

            gdpr.childPmId?.let { dataStorage.gdprChildPmId = it }
            gdpr.sampleRate.let { newRate ->
                if (!newRate.toDouble().almostSameAs(dataStorage.gdprSampleRate)) {
                    dataStorage.gdprSampleRate = newRate.toDouble()
                    dataStorage.gdprSampled = null
                }
            }
        }

        response.usnat?.let { usnat ->
            usNatConsentData = usNatConsentData?.copy(applies = usnat.applies)
            usnat.sampleRate.let { newRate ->
                if (!newRate.toDouble().almostSameAs(dataStorage.usnatSampleRate)) {
                    dataStorage.usnatSampleRate = newRate.toDouble()
                    dataStorage.usnatSampled = null
                }
            }
        }
    }

    override fun hasUsnatApplicableSectionsChanged(usnatMetaData: MetaDataResponse.MetaDataResponseUSNat?): Boolean {
        if (!spConfig.isIncluded(USNAT) || metaDataResp?.usnat == null || usnatMetaData == null) {
            return false
        }
        return metaDataResp?.usnat?.applicableSections != usnatMetaData.applicableSections
    }

    override fun hasGdprVendorListIdChanged(gdprVendorListId: String?): Boolean {
        val storedGdprVendorListId = metaDataResp?.gdpr?.vendorListId
        return gdprVendorListId != null && storedGdprVendorListId != null &&
            storedGdprVendorListId != gdprVendorListId
    }

    override fun hasUsNatVendorListIdChanged(usNatVendorListId: String?): Boolean {
        val storedUsNatVendorListId = metaDataResp?.usnat?.vendorListId
        return usNatVendorListId != null && storedUsNatVendorListId != null &&
            storedUsNatVendorListId != usNatVendorListId
    }

    override fun reConsentGdpr(
        additionsChangeDate: String?,
        legalBasisChangeDate: String?
    ): ConsentStatus? {
        val dataRecordedConsent = gdprConsentStatus?.dateCreated

        val updatedGdprConsentStatus = gdprConsentStatus?.consentStatus

        return if (dataRecordedConsent != null &&
            updatedGdprConsentStatus != null &&
            additionsChangeDate != null &&
            legalBasisChangeDate != null
        ) {
            val dataRecordedConsentDate = formatter.parse(dataRecordedConsent)
            val additionsChangeDateDate = formatter.parse(additionsChangeDate)
            val legalBasisChangeDateConsentDate = formatter.parse(legalBasisChangeDate)

            val creationLessThanAdditions = dataRecordedConsentDate?.before(additionsChangeDateDate) ?: false
            val creationLessThanLegalBasis = dataRecordedConsentDate?.before(legalBasisChangeDateConsentDate) ?: false

            val shouldReconsent = creationLessThanAdditions || creationLessThanLegalBasis

            val map = mapOf(
                "dataRecordedConsentDate" to "$dataRecordedConsentDate",
                "additionsChangeDateDate" to "$additionsChangeDateDate",
                "legalBasisChangeDateConsentDate" to "$legalBasisChangeDateConsentDate",
                "creationLessThanAdditions OR creationLessThanLegalBasis" to "$shouldReconsent",
            )

            logger?.computation(
                tag = "Reconsent updatedGdprConsentStatus",
                msg = JSONObject(map).toString(),
                json = JSONObject(map)
            )

            if (!shouldReconsent) return null

            if (creationLessThanAdditions) {
                updatedGdprConsentStatus.vendorListAdditions = true
            }
            if (creationLessThanLegalBasis) {
                updatedGdprConsentStatus.legalBasisChanges = true
            }
            if (updatedGdprConsentStatus.consentedAll == true) {
                updatedGdprConsentStatus.granularStatus?.previousOptInAll = true
                updatedGdprConsentStatus.consentedAll = false
            }

            updatedGdprConsentStatus
        } else null
    }

    override fun reConsentUsnat(
        additionsChangeDate: String?
    ): USNatConsentStatus? {

        val dataRecordedConsent = usNatConsentData?.dateCreated

        val updatedUSNatConsentStatus = usNatConsentData?.consentStatus

        return if (dataRecordedConsent != null &&
            updatedUSNatConsentStatus != null &&
            additionsChangeDate != null
        ) {
            val dataRecordedConsentDate = formatter.parse(dataRecordedConsent)
            val additionsChangeDateDate = formatter.parse(additionsChangeDate)
            val creationLessThanAdditions = dataRecordedConsentDate?.before(additionsChangeDateDate) ?: false

            val map = mapOf(
                "dataRecordedConsentDate" to "$dataRecordedConsentDate",
                "additionsChangeDateDate" to "$additionsChangeDateDate",
                "creationLessThanAdditions" to "$creationLessThanAdditions",
            )

            logger?.computation(
                tag = "Reconsent updateUSNATConsent",
                msg = JSONObject(map).toString(),
                json = JSONObject(map)
            )

            if (!creationLessThanAdditions) return null

            updatedUSNatConsentStatus.vendorListAdditions = true

            if (updatedUSNatConsentStatus.consentedToAll == true) {
                updatedUSNatConsentStatus.granularStatus?.previousOptInAll = true
                updatedUSNatConsentStatus.consentedToAll = false
            }

            updatedUSNatConsentStatus
        } else null
    }

    override fun handleOldLocalData() {
        if (dataStorage.preference.contains(LOCAL_STATE) || dataStorage.preference.contains(LOCAL_STATE_OLD)) {
            dataStorage.preference
                .edit().apply {
                    remove(LOCAL_STATE)
                    remove(LOCAL_STATE_OLD)
                    remove(KEY_GDPR_APPLIES_OLD)
                    remove(KEY_GDPR_MESSAGE_SUBCATEGORY_OLD)
                    remove("key_property_id")
                    remove("key_ccpa")
                    remove("key_gdpr")
                    remove("ccpa_consent_resp")
                    remove("gdpr_consent_resp")
                }
                .apply()
        }
    }

    override var gdprDateCreated: String?
        get() = dataStorage.gdprDateCreated
        set(value) {
            dataStorage.gdprDateCreated = value
        }

    override var ccpaDateCreated: String?
        get() = dataStorage.ccpaDateCreated
        set(value) {
            dataStorage.ccpaDateCreated = value
        }

    override var metaDataResp: MetaDataResponse?
        get() {
            return dataStorage.metaDataResp?.let { JsonConverter.converter.decodeFromString<MetaDataResponse>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.metaDataResp = serialised
        }

    override var choiceResp: ChoiceResp?
        get() {
            return dataStorage.choiceResp?.let { JsonConverter.converter.decodeFromString<ChoiceResp>(it) }
        }
        set(value) {
            val serialised = value?.let { JsonConverter.converter.encodeToString(value) }
            dataStorage.choiceResp = serialised
        }

    override var dataRecordedConsent: Instant?
        get() {
            return dataStorage.dataRecordedConsent?.let { Instant.parse(it) }
        }
        set(value) {
            dataStorage.dataRecordedConsent = value?.toString()
        }

    override fun getGdprPvDataBody(messageReq: MessagesParamReq): PvDataRequest.GDPR {
        return PvDataRequest.GDPR(
            applies = metaDataResp?.gdpr?.applies ?: false,
            uuid = gdprConsentStatus?.uuid,
            accountId = messageReq.accountId.toInt(),
            propertyId = messageReq.propertyId.toInt(),
            consentStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus(
                rejectedAny = gdprConsentStatus?.consentStatus?.rejectedAny,
                rejectedLI = gdprConsentStatus?.consentStatus?.rejectedLI,
                consentedAll = gdprConsentStatus?.consentStatus?.consentedAll,
                consentedToAny = gdprConsentStatus?.consentStatus?.consentedToAny,
                hasConsentData = gdprConsentStatus?.consentStatus?.hasConsentData,
                legalBasisChanges = gdprConsentStatus?.consentStatus?.legalBasisChanges,
                vendorListAdditions = gdprConsentStatus?.consentStatus?.vendorListAdditions,
                granularStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus.ConsentStatusGranularStatus(
                    defaultConsent = gdprConsentStatus?.consentStatus?.granularStatus?.defaultConsent,
                    previousOptInAll = gdprConsentStatus?.consentStatus?.granularStatus?.previousOptInAll,
                    purposeConsent = gdprConsentStatus?.consentStatus?.granularStatus?.purposeConsent.toString(),
                    purposeLegInt = gdprConsentStatus?.consentStatus?.granularStatus?.purposeLegInt.toString(),
                    vendorConsent = gdprConsentStatus?.consentStatus?.granularStatus?.vendorConsent.toString(),
                    vendorLegInt = gdprConsentStatus?.consentStatus?.granularStatus?.vendorLegInt.toString(),
                ),
            ),
            pubData = messageReq.pubData,
            sampleRate = metaDataResp?.gdpr?.sampleRate,
            euconsent = gdprConsentStatus?.euconsent,
            msgId = gdprMessageMetaData?.messageId,
            categoryId = gdprMessageMetaData?.categoryId?.code,
            subCategoryId = gdprMessageMetaData?.subCategoryId?.code,
            prtnUUID = gdprMessageMetaData?.prtnUUID
        )
    }

    override fun getCcpaPvDataBody(messageReq: MessagesParamReq): PvDataRequest.CCPA {
        return PvDataRequest.CCPA(
            applies = metaDataResp?.ccpa?.applies ?: false,
            uuid = ccpaConsentStatus?.uuid,
            accountId = messageReq.accountId.toInt(),
            propertyId = messageReq.propertyId.toInt(),
            consentStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus(
                rejectedAll = ccpaConsentStatus?.rejectedAll,
                consentedAll = ccpaConsentStatus?.consentedAll,
                rejectedVendors = ccpaConsentStatus?.rejectedVendors,
                rejectedCategories = ccpaConsentStatus?.rejectedCategories
            ),
            pubData = messageReq.pubData,
            messageId = ccpaMessageMetaData?.messageId,
            sampleRate = metaDataResp?.ccpa?.sampleRate
        )
    }

    override fun getUsNatPvDataBody(messageReq: MessagesParamReq): PvDataRequest.USNat {
        return PvDataRequest.USNat(
            applies = metaDataResp?.usnat?.applies ?: false,
            uuid = usNatConsentData?.uuid,
            accountId = messageReq.accountId.toInt(),
            propertyId = messageReq.propertyId.toInt(),
            consentStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus(
                rejectedAny = usNatConsentData?.consentStatus?.rejectedAny,
                consentedToAll = usNatConsentData?.consentStatus?.consentedToAll,
                consentedToAny = usNatConsentData?.consentStatus?.consentedToAny,
                granularStatus = com.sourcepoint.mobile_core.models.consents.ConsentStatus.ConsentStatusGranularStatus(
                    sellStatus = usNatConsentData?.consentStatus?.granularStatus?.sellStatus,
                    shareStatus = usNatConsentData?.consentStatus?.granularStatus?.shareStatus,
                    sensitiveDataStatus = usNatConsentData?.consentStatus?.granularStatus?.sensitiveDataStatus,
                    gpcStatus = usNatConsentData?.consentStatus?.granularStatus?.gpcStatus,
                    defaultConsent = usNatConsentData?.consentStatus?.granularStatus?.defaultConsent,
                    previousOptInAll = usNatConsentData?.consentStatus?.granularStatus?.previousOptInAll,
                    purposeConsent = usNatConsentData?.consentStatus?.granularStatus?.purposeConsent
                ),
                hasConsentData = usNatConsentData?.consentStatus?.hasConsentData,
                vendorListAdditions = usNatConsentData?.consentStatus?.vendorListAdditions,
            ),
            pubData = messageReq.pubData,
            sampleRate = metaDataResp?.usnat?.sampleRate,
            msgId = usNatConsentData?.messageMetaData?.messageId,
            categoryId = usNatConsentData?.messageMetaData?.categoryId?.code,
            subCategoryId = usNatConsentData?.messageMetaData?.subCategoryId?.code,
            prtnUUID = usNatConsentData?.messageMetaData?.prtnUUID
        )
    }

    override val isGdprExpired: Boolean
        get() {
            val gdprExpirationDate = dataStorage.gdprExpirationDate?.let { formatter.parse(it) } ?: return false
            val currentDate = Date()
            return currentDate.after(gdprExpirationDate)
        }

    override val isCcpaExpired: Boolean
        get() {
            val ccpaExpirationDate = dataStorage.ccpaExpirationDate?.let { formatter.parse(it) } ?: return false
            val currentDate = Date()
            return currentDate.after(ccpaExpirationDate)
        }

    override val isUsnatExpired: Boolean
        get() {
            val usnatExpirationDate = usNatConsentData?.expirationDate?.let { formatter.parse(it) } ?: return false
            val currentDate = Date()
            return currentDate.after(usnatExpirationDate)
        }

    override val storeChoiceResp: ChoiceResp
        get() = ChoiceResp(
            ccpa = ccpaConsentStatus,
            gdpr = gdprConsentStatus,
            usNat = usNatConsentData
        )

    override fun deleteExpiredConsents() {
        if (isUsnatExpired) dataStorage.deleteUsNatConsent()
        if (isCcpaExpired) dataStorage.deleteCcpaConsent()
        if (isGdprExpired) dataStorage.deleteGdprConsent()

        logger?.computation(
            tag = "Expiration Date",
            msg = """
                isGdprExpired[$isGdprExpired] 
                isCcpaExpired[$isCcpaExpired] 
                isUsnatExpired[$isUsnatExpired] 
            """.trimIndent()
        )
    }
}

fun Double.almostSameAs(other: Double) = abs(this - other) < 0.000001
