package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepoint.cmplibrary.creation.config
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepoint.cmplibrary.model.exposed.TargetingParam
import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource.Companion.buildSPCampaign
import com.sourcepointmeta.metaapp.db.MetaAppDB
import com.sourcepointmeta.metaapp.util.check
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import kotlin.RuntimeException

internal interface LocalDataSource {

    val propertyEvents: Flow<List<Property>>
    val logEvents: Flow<MetaLog>

    suspend fun fetchProperties(): Either<List<Property>>
    suspend fun fetchPropertyByName(name: String): Either<Property>
    fun fetchPropertyByNameSync(name: String): Property
    fun fetchLogsByPropertyName(propertyName: String): Either<List<MetaLog>>
    suspend fun fetchTargetingParams(propName: String): Either<List<MetaTargetingParam>>
    suspend fun storeOrUpdateProperty(property: Property): Either<Property>
    suspend fun storeOrUpdateLog(log: MetaLog)
    suspend fun propertyCount(): Either<Int>
    suspend fun updateProperty(property: Property)
    suspend fun deleteAll()
    suspend fun deleteByPropertyName(name: String)
    fun deleteTargetingParameter(propName: String, campaignType: CampaignType, key: String)
    fun getSPConfig(pName: String): Either<SpConfig>

    companion object {
        fun buildSPCampaign(
            campaignType: CampaignType,
            campaignStatus: Set<StatusCampaign>,
            list: List<MetaTargetingParam>
        ): List<TargetingParam>? {
            val campaign: CampaignType? = campaignStatus
                .firstOrNull { it.campaignType == campaignType && it.enabled }
                ?.campaignType
            return campaign?.let { ct ->
                list
                    .filter { it.campaign == ct }
                    .map { TargetingParam(it.key, it.value) }
            }
        }
    }
}

internal fun LocalDataSource.Companion.create(db: MetaAppDB): LocalDataSource = LocalDataSourceImpl(db)

private class LocalDataSourceImpl(
    db: MetaAppDB
) : LocalDataSource {

    val cQueries = db.campaignQueries
    val lQueries = db.loggerQueries

    private val singleThreadDispatcher = Executors.newFixedThreadPool(1).asCoroutineDispatcher()

    val mutableFlow: MutableSharedFlow<List<Property>> = MutableSharedFlow()
    override val propertyEvents: Flow<List<Property>> = mutableFlow.asSharedFlow()

    val mutableLogFlow: MutableSharedFlow<MetaLog> = MutableSharedFlow()
    override val logEvents: Flow<MetaLog> = mutableLogFlow

    override suspend fun fetchProperties(): Either<List<Property>> = coroutineScope {
        check {
            cQueries
                .selectAllProperties()
                .executeAsList()
                .map { row ->
                    val tp =
                        cQueries.getTargetingParams(row.property_name)
                            .executeAsList()
                            .map { it.toTargetingParam() }
                    val statusCampaignList = cQueries.selectStatusCampaignByPropertyName(row.property_name)
                        .executeAsList()
                        .map { it.toStatusCampaign() }
                        .toSet()
                    row.toProperty(tp, statusCampaignList)
                }
        }
    }

    private fun fetchPropByName(name: String): Either<Property> = check {
        cQueries
            .selectPropertyByName(name)
            .executeAsOne()
            .let { row ->
                val tp =
                    cQueries.getTargetingParams(row.property_name)
                        .executeAsList()
                        .map { it.toTargetingParam() }
                val statusCampaignList = cQueries.selectStatusCampaignByPropertyName(row.property_name)
                    .executeAsList()
                    .map { it.toStatusCampaign() }
                    .toSet()
                row.toProperty(tp, statusCampaignList)
            }
    }

    override suspend fun fetchPropertyByName(name: String): Either<Property> = coroutineScope {
        fetchPropByName(name)
    }

    override fun fetchPropertyByNameSync(name: String): Property {
        return fetchPropByName(name).getOrNull()!!
    }

    override fun fetchLogsByPropertyName(propertyName: String): Either<List<MetaLog>> {
        return check {
            lQueries.selectAllLogsByPropertyName(propertyName).executeAsList().map { it.toMetaLog() }
        }
    }

    override suspend fun fetchTargetingParams(propName: String): Either<List<MetaTargetingParam>> = coroutineScope {
        check {
            cQueries.selectTargetingParametersByPropertyName(propName).executeAsList().map { it.toTargetingParam() }
        }
    }

    override suspend fun propertyCount(): Either<Int> = check {
        cQueries.selectAllProperties().executeAsList().size
    }

    override suspend fun storeOrUpdateProperty(property: Property): Either<Property> = coroutineScope {
        cQueries.run {
            transactionWithResult {
                insertProperty(
                    property_id = property.propertyId,
                    auth_Id = property.authId,
                    message_language = property.messageLanguage,
                    pm_tab = property.pmTab,
                    account_id = property.accountId,
                    is_staging = if (property.is_staging) 1 else 0,
                    property_name = property.propertyName,
                    message_type = property.messageType,
                    timestamp = property.timestamp,
                    ccpa_pm_id = property.ccpaPmId,
                    gdpr_pm_id = property.gdprPmId
                )
                deleteTargetingParameterByPropName(property.propertyName)
                property.targetingParameters.forEach {
                    insertTargetingParameter(
                        property_name = property.propertyName,
                        key = it.key,
                        value = it.value,
                        campaign = it.campaign.name
                    )
                }
                property.statusCampaignSet.forEach { sc ->
                    insertStatusCampaign(
                        property_name = property.propertyName,
                        campaign_type = sc.campaignType.name,
                        enabled = sc.enabled.toValueDB(),
                    )
                }

                fetchPropByName(property.propertyName)
            }
        }
    }

    override suspend fun storeOrUpdateLog(log: MetaLog) = coroutineScope<Unit> {
        launch(singleThreadDispatcher) {
            log.run {
                lQueries
                    .insertLog(
                        id = id,
                        tag = tag,
                        timestamp = timestamp,
                        type = type,
                        property_name = propertyName,
                        message = message,
                        json_body = jsonBody,
                        log_session = logSession,
                        status_req = statusReq
                    )
                lQueries
                    .lastInsertLog()
                    .executeAsList()
                    .firstOrNull()
                    ?.toMetaLog()
                    ?.let { mutableLogFlow.emit(it) }
            }
        }
    }

    override suspend fun updateProperty(property: Property) {
    }

    override suspend fun deleteAll() {
        cQueries.deleteTargetingParameters()
        cQueries.deleteAllProperties()
        cQueries.deleteStatusCampaign()
    }

    override suspend fun deleteByPropertyName(name: String) {
        cQueries.run {
            deletePropertyByName(name)
            deleteTargetingParameterByPropName(name)
            deleteStatusCampaignByPropName(name)
        }
    }

    override fun deleteTargetingParameter(propName: String, campaignType: CampaignType, key: String) {
        cQueries.deleteTPByPropNameCampaignKey(
            property_name = propName,
            campaign = campaignType.name,
            key = key
        )
    }

    override fun getSPConfig(pName: String): Either<SpConfig> {
        return check {
            fetchPropByName(pName)
                .getOrNull()
                ?.let { p ->
                    config {
                        accountId = p.accountId.toInt()
                        propertyName = p.propertyName
                        messLanguage =
                            MessageLanguage.values().find { it.name == p.messageLanguage } ?: MessageLanguage.ENGLISH
                        buildSPCampaign(CampaignType.GDPR, p.statusCampaignSet, p.targetingParameters)
                            ?.let { spc -> addCampaign(CampaignType.GDPR, spc) }
                        buildSPCampaign(CampaignType.CCPA, p.statusCampaignSet, p.targetingParameters)
                            ?.let { spc -> addCampaign(CampaignType.CCPA, spc) }
                    }
                }
                ?: throw RuntimeException("Inconsistent state! LocalDataSource.getSPConfig cannot have a SpConfig null!!!")
        }
    }
}
