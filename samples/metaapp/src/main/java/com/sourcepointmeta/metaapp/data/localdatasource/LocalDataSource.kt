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
import comsourcepointmetametaappdb.CampaignQueries
import comsourcepointmetametaappdb.Property_
import comsourcepointmetametaappdb.Status_campaign
import comsourcepointmetametaappdb.Targeting_param
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.RuntimeException

internal interface LocalDataSource {

    val update: Flow<List<Property>>

    suspend fun fetchProperties(): Either<List<Property>>
    suspend fun fetchPropertyByName(name: String): Either<Property>
    suspend fun fetchTargetingParams(propName: String): Either<List<MetaTargetingParam>>
    suspend fun storeOrUpdateProperty(property: Property): Either<Property>
    suspend fun updateProperty(property: Property)
    suspend fun deleteAll()
    suspend fun deleteByPropertyName(name: String)
    fun getSPConfig(pName: String): Either<SpConfig>

    companion object {
        fun buildSPCampaign(
            campaignType: CampaignType,
            campaignStatus: StatusCampaign,
            list: List<MetaTargetingParam>
        ): Pair<CampaignType, List<TargetingParam>>? {
            if (campaignType == CampaignType.GDPR) {
                return list
                    .filter { it.campaign == CampaignType.GDPR }
                    .map { TargetingParam(it.key, it.value) }
                    .let { Pair(CampaignType.GDPR, it) }
            }
            if (campaignType == CampaignType.CCPA) {
                return list
                    .filter { it.campaign == CampaignType.CCPA }
                    .map { TargetingParam(it.key, it.value) }
                    .let { Pair(CampaignType.CCPA, it) }
            }
            return null
        }
    }
}

internal fun LocalDataSource.Companion.create(db: MetaAppDB): LocalDataSource = LocalDataSourceImpl(db)

private class LocalDataSourceImpl(
    db: MetaAppDB
) : LocalDataSource {

    val cQueries = db.campaignQueries

    val mutableFlow: MutableSharedFlow<List<Property>> = MutableSharedFlow()
    override val update: Flow<List<Property>> = mutableFlow.asSharedFlow()

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
                    val statusCampaign = cQueries.selectStatusCampaignByPropertyName(row.property_name)
                        .executeAsOneOrNull()?.toStatusCampaign() ?: throw RuntimeException()
                    row.toProperty(tp, statusCampaign)
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
                val statusCampaign = cQueries.selectStatusCampaignByPropertyName(row.property_name)
                    .executeAsOneOrNull()
                    ?.toStatusCampaign() ?: throw RuntimeException()
                row.toProperty(tp, statusCampaign)
            }
    }

    override suspend fun fetchPropertyByName(name: String): Either<Property> = coroutineScope {
        fetchPropByName(name)
    }

    override suspend fun fetchTargetingParams(propName: String): Either<List<MetaTargetingParam>> = coroutineScope {
        check {
            cQueries.selectTargetingParametersByPropertyName(propName).executeAsList().map { it.toTargetingParam() }
        }
    }

    override suspend fun storeOrUpdateProperty(property: Property): Either<Property> = coroutineScope {
        cQueries.run {
            transactionWithResult {
                insertProperty(
                    property_id = property.propertyId,
                    pm_id = property.pmId,
                    auth_Id = property.authId,
                    message_language = property.messageLanguage,
                    pm_tab = property.pmTab,
                    account_id = property.accountId,
                    is_staging = if (property.is_staging) 1 else 0,
                    property_name = property.propertyName,
                    message_type = property.messageType,
                    timestamp = property.timestamp
                )
                property.targetingParameters.forEach {
                    insertTargetingParameter(
                        property_name = property.propertyName,
                        key = it.key,
                        value = it.value,
                        campaign = it.campaign.name
                    )
                }
                insertStatusCampaign(
                    property_name = property.propertyName,
                    campaign_type = CampaignType.GDPR.name,
                    enabled = 1,
                )
                fetchPropByName(property.propertyName)
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
        }
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
                        buildSPCampaign(CampaignType.GDPR, StatusCampaign("", CampaignType.GDPR, false), p.targetingParameters)
                            ?.let { spc -> addCampaign(spc.first, spc.second) }
                        buildSPCampaign(CampaignType.GDPR, StatusCampaign("", CampaignType.GDPR, false), p.targetingParameters)
                            ?.let { spc -> addCampaign(spc.first, spc.second) }
                    }
                }
                ?: throw RuntimeException("Inconsistent state! LocalDataSource.getSPConfig cannot have a SpConfig null!!!")
        }
    }
}

private fun Targeting_param.toTargetingParam() = MetaTargetingParam(
    propertyName = property_name,
    value = value,
    key = key,
    campaign = CampaignType.values().find { it.name == campaign } ?: CampaignType.GDPR
)

private fun Property_.toProperty(tp: List<MetaTargetingParam>, statusCampaign: StatusCampaign) = Property(
    propertyId = property_id,
    propertyName = property_name,
    is_staging = is_staging != 0L,
    accountId = account_id,
    pmTab = pm_tab,
    messageLanguage = message_language,
    authId = auth_Id,
    pmId = pm_id,
    targetingParameters = tp,
    statusCampaignSet = setOf(statusCampaign),
    messageType = message_type,
    timestamp = timestamp
)

private fun CampaignQueries.getTargetingParams(propName: String) =
    this.selectTargetingParametersByPropertyName(propName)

private fun Status_campaign.toStatusCampaign() = StatusCampaign(
    propertyName = property_name,
    enabled = enabled != 0L,
    campaignType = CampaignType.GDPR,
)

private fun Boolean.toValueDB() = when (this) {
    true -> 1L
    false -> 0L
}
