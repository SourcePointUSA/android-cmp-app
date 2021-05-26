package com.sourcepointmeta.metaapp.data.localdatasource

import com.sourcepointmeta.metaapp.core.Either
import com.sourcepointmeta.metaapp.db.MetaAppDB
import com.sourcepointmeta.metaapp.util.check
import comsourcepointmetametaappdb.CampaignQueries
import comsourcepointmetametaappdb.Property_
import comsourcepointmetametaappdb.Targeting_param
import kotlinx.coroutines.coroutineScope

internal interface LocalDataSource {
    suspend fun fetchProperties(): Either<List<Property>>
    suspend fun fetchPropertyByName(name: String): Either<Property>
    suspend fun fetchTargetingParams(propName: String): Either<List<TargetingParam>>
    suspend fun storeOrUpdateProperty(property: Property)
    suspend fun updateProperty(property: Property)
    suspend fun deleteAll()
    suspend fun deleteByPropertyName(name: String)

    companion object
}

internal fun LocalDataSource.Companion.create(db: MetaAppDB): LocalDataSource = LocalDataSourceImpl(db)

private class LocalDataSourceImpl(
    db: MetaAppDB
) : LocalDataSource {

    val cQueries = db.campaignQueries

    override suspend fun fetchProperties(): Either<List<Property>> = coroutineScope {
        check {
            cQueries
                .selectAllProperties()
                .executeAsList()
                .map { row ->
                    val tp =
                        cQueries.getTargetingParams(row.property_name).executeAsList().map { it.toTargetingParam() }
                    row.toProperty(tp)
                }
        }
    }

    override suspend fun fetchPropertyByName(name: String): Either<Property> = coroutineScope {
        check {
            cQueries
                .selectPropertyByName(name)
                .executeAsOne()
                .let { row ->
                    val tp =
                        cQueries.getTargetingParams(row.property_name).executeAsList().map { it.toTargetingParam() }
                    row.toProperty(tp)
                }
        }
    }

    override suspend fun fetchTargetingParams(propName: String): Either<List<TargetingParam>> = coroutineScope {
        check {
            cQueries.selectTargetingParametersByPropertyName(propName).executeAsList().map { it.toTargetingParam() }
        }
    }

    override suspend fun storeOrUpdateProperty(property: Property) {
        cQueries.run {
            transaction {
                insertProperty(
                    property_id = property.property_id,
                    pm_id = property.pm_id,
                    auth_Id = property.auth_Id,
                    message_language = property.message_language,
                    pm_tab = property.pm_tab,
                    account_id = property.account_id,
                    is_staging = if (property.is_staging) 1 else 0,
                    property_name = property.property_name
                )
                property.targetingParameters.forEach {
                    insertTargetingParameter(
                        property_name = property.property_name,
                        key = it.key,
                        value = it.value
                    )
                }
            }
        }
    }

    override suspend fun updateProperty(property: Property) {
    }

    override suspend fun deleteAll() {
        cQueries.deleteTargetingParameters()
        cQueries.deleteAllProperties()
    }

    override suspend fun deleteByPropertyName(name: String) {
        cQueries.run {
            deletePropertyByName(name)
            deleteTargetingParameterByPropName(name)
        }
    }
}

private fun Targeting_param.toTargetingParam() = TargetingParam(
    propertyName = property_name,
    value = value,
    key = key
)

private fun Property_.toProperty(tp: List<TargetingParam>) = Property(
    property_id = property_id,
    property_name = property_name,
    is_staging = is_staging != 0L,
    account_id = account_id,
    pm_tab = pm_tab,
    message_language = message_language,
    auth_Id = auth_Id,
    pm_id = pm_id,
    targetingParameters = tp
)

private fun CampaignQueries.getTargetingParams(propName: String) =
    this.selectTargetingParametersByPropertyName(propName)
