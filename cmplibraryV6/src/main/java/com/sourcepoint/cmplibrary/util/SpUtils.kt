@file:JvmName("SpUtils")

package com.sourcepoint.cmplibrary.util

import android.content.Context
import com.sourcepoint.cmplibrary.SpCacheObjet.fetchOrStore
import com.sourcepoint.cmplibrary.campaign.CampaignManager
import com.sourcepoint.cmplibrary.campaign.create
import com.sourcepoint.cmplibrary.core.getOrNull
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.model.SPCCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.SPGDPRConsent
import com.sourcepoint.cmplibrary.exception.Legislation
import com.sourcepoint.cmplibrary.model.SPConsents

fun userConsents(context: Context): SPConsents {
    val cm: CampaignManager = createStorage(context)
    return userConsents(cm)
}

fun gdprApplies(context: Context): Boolean {
    val cm: CampaignManager = createStorage(context)
    return cm
        .getGdpr()
        .getOrNull()
        ?.gdprApplies
        ?: false
}

fun ccpaApplies(context: Context): Boolean {
    val cm: CampaignManager = createStorage(context)
    return cm
        .getCcpa()
        .getOrNull()
        ?.ccpaApplies
        ?: false
}

fun clearAllData(context: Context) {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    val dataStorageCcpa = DataStorageCcpa.create(context)
    DataStorage.create(context, dataStorageGdpr, dataStorageCcpa).clearAll()
}

internal fun userConsents(
    campaignManager: CampaignManager
): SPConsents {

    return SPConsents(
        ccpa = campaignManager.getCCPAConsent().getOrNull()?.let {
            SPCCPAConsent(
                applies = campaignManager.isAppliedCampaign(Legislation.GDPR),
                consent = it
            )
        },
        gdpr = campaignManager.getGDPRConsent().getOrNull()?.let {
            SPGDPRConsent(
                applies = campaignManager.isAppliedCampaign(Legislation.CCPA),
                consent = it
            )
        }
    )
}

internal fun createStorage(context: Context): CampaignManager {
    val dataStorageGdpr = fetchOrStore(DataStorageGdpr::class.java) { DataStorageGdpr.create(context) }
    val dataStorageCcpa = fetchOrStore(DataStorageCcpa::class.java) { DataStorageCcpa.create(context) }
    val dataStorage = fetchOrStore(DataStorage::class.java) { DataStorage.create(context, dataStorageGdpr, dataStorageCcpa) }
    return fetchOrStore(CampaignManager::class.java) { CampaignManager.create(dataStorage) }
}
