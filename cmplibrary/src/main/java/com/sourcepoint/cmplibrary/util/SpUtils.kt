@file:JvmName("SpUtils")

package com.sourcepoint.cmplibrary.util

import android.content.Context
import com.sourcepoint.cmplibrary.SpCacheObjet.fetchOrStore
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.local.getCCPAConsent
import com.sourcepoint.cmplibrary.data.local.getGDPRConsent
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SPCCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SPGDPRConsent

fun userConsents(context: Context): SPConsents {
    val dataStorageGdpr = fetchOrStore(DataStorageGdpr::class.java) { DataStorageGdpr.create(context) }
    val dataStorageCcpa = fetchOrStore(DataStorageCcpa::class.java) { DataStorageCcpa.create(context) }
    val dataStorage = fetchOrStore(DataStorage::class.java) { DataStorage.create(context, dataStorageGdpr, dataStorageCcpa) }
    return userConsents(dataStorage)
}

fun campaignApplies(context: Context, campaign: CampaignType): Boolean {
    return when (campaign) {
        CampaignType.GDPR -> DataStorageGdpr.create(context).gdprApplies
        CampaignType.CCPA -> DataStorageCcpa.create(context).ccpaApplies
    }
}

fun clearAllData(context: Context) {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    val dataStorageCcpa = DataStorageCcpa.create(context)
    DataStorage.create(context, dataStorageGdpr, dataStorageCcpa).clearAll()
}

internal fun userConsents(
    dataStorage: DataStorage
): SPConsents {

    return SPConsents(
        ccpa = dataStorage.getCCPAConsent().getOrNull()?.let {
            SPCCPAConsent(
                consent = it
            )
        },
        gdpr = dataStorage.getGDPRConsent().getOrNull()?.let {
            SPGDPRConsent(
                consent = it
            )
        }
    )
}
