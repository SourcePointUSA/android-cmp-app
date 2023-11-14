@file:JvmName("SpUtils")

package com.sourcepoint.cmplibrary.util

import android.content.Context
import com.sourcepoint.cmplibrary.SpCacheObjet.fetchOrStore
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.DataStorageUSNat
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.converter.JsonConverter
import com.sourcepoint.cmplibrary.data.network.converter.converter
import com.sourcepoint.cmplibrary.data.network.model.optimized.CcpaCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.GdprCS
import com.sourcepoint.cmplibrary.data.network.model.optimized.toCCPAConsentInternal
import com.sourcepoint.cmplibrary.data.network.model.optimized.toGDPRUserConsent
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.exception.Logger
import com.sourcepoint.cmplibrary.model.exposed.CcpaStatus
import com.sourcepoint.cmplibrary.model.exposed.SPCCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SPGDPRConsent
import kotlinx.serialization.decodeFromString

private const val DEFAULT_CCPA_USP_STRING = "1---"

internal fun updateCcpaUspString(
    ccpaCS: CcpaCS,
    logger: Logger? = null,
): String {
    val applies: Boolean? = ccpaCS.applies
    val ccpaStatus: CcpaStatus? = ccpaCS.status
    val signedLspa: Boolean? = ccpaCS.signedLspa
    return if (applies == true) {
        val specificationVersion = "1"
        val opportunityToOptOut = "Y"
        val optOutSale = when (ccpaStatus) {
            CcpaStatus.rejectedAll, CcpaStatus.rejectedSome -> "Y"
            else -> "N"
        }
        val lspaCoveredTransaction = if (signedLspa == null || signedLspa == false) "N" else "Y"
        val usPString = "$specificationVersion$opportunityToOptOut$optOutSale$lspaCoveredTransaction"
        logger?.computation(
            tag = " Ccpa UspString",
            msg = """
                spec Version[1] - oppToOptOut[Y]
                ccpaStatus[$ccpaStatus] => optOutSale[$optOutSale]
                signedLspa[$signedLspa] => LspaCovTransac[$lspaCoveredTransaction]
                usPString[$usPString]
            """.trimIndent()
        )
        usPString
    } else {
        logger?.computation(
            tag = " Ccpa UspString",
            msg = """
                applies[$applies]
                $DEFAULT_CCPA_USP_STRING
            """.trimIndent()
        )
        DEFAULT_CCPA_USP_STRING
    }
}

fun userConsents(context: Context): SPConsents {
    val dataStorageGdpr = fetchOrStore(DataStorageGdpr::class.java) { DataStorageGdpr.create(context) }
    val dataStorageCcpa = fetchOrStore(DataStorageCcpa::class.java) { DataStorageCcpa.create(context) }
    val dataStorageUSNat = fetchOrStore(DataStorageUSNat::class.java) { DataStorageUSNat.create(context) }
    val dataStorage = fetchOrStore(DataStorage::class.java) { DataStorage.create(context, dataStorageGdpr, dataStorageCcpa, dataStorageUSNat) }
    return userConsents(dataStorage)
}

fun campaignApplies(context: Context, campaign: CampaignType): Boolean {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    val dataStorageCcpa = DataStorageCcpa.create(context)
    val dataStorageUSNat = DataStorageUSNat.create(context)
    val dataStorage = DataStorage.create(
        context = context,
        dsGdpr = dataStorageGdpr,
        dsCcpa = dataStorageCcpa,
        dsUsNat = dataStorageUSNat,
    )
    return when (campaign) {
        CampaignType.GDPR -> dataStorage.gdprApplies
        CampaignType.CCPA -> dataStorage.ccpaApplies
        CampaignType.USNAT -> dataStorage.usNatApplies
    }
}

fun clearAllData(context: Context) {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    val dataStorageCcpa = DataStorageCcpa.create(context)
    val dataStorageUSNat = DataStorageUSNat.create(context)
    DataStorage.create(context, dataStorageGdpr, dataStorageCcpa, dataStorageUSNat).clearAll()
}

internal fun userConsents(
    dataStorage: DataStorage
): SPConsents {

    return SPConsents(
        ccpa = dataStorage.ccpaConsentStatus
            ?.let { check { JsonConverter.converter.decodeFromString<CcpaCS>(it) }.getOrNull() }
            ?.copy(applies = dataStorage.ccpaApplies)
            ?.toCCPAConsentInternal()
            ?.let {
                SPCCPAConsent(
                    consent = it
                )
            },
        gdpr = dataStorage.gdprConsentStatus
            ?.let { check { JsonConverter.converter.decodeFromString<GdprCS>(it) }.getOrNull() }
            ?.copy(applies = dataStorage.gdprApplies)
            ?.toGDPRUserConsent()
            ?.let {
                SPGDPRConsent(
                    consent = it
                )
            }
    )
}
