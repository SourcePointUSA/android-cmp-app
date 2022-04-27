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
import com.sourcepoint.cmplibrary.model.MessageLanguage
import com.sourcepoint.cmplibrary.model.exposed.SPCCPAConsent
import com.sourcepoint.cmplibrary.model.exposed.SPConsents
import com.sourcepoint.cmplibrary.model.exposed.SPGDPRConsent
import com.sourcepoint.cmplibrary.model.exposed.SpConfig

fun userConsents(context: Context, spConfig: SpConfig): SPConsents {
    val cm: CampaignManager = createStorage(context, spConfig)
    return userConsents(cm)
}

fun gdprApplies(context: Context): Boolean {
    return DataStorageGdpr.create(context).gdprApplies
}

fun ccpaApplies(context: Context): Boolean {
    return DataStorageCcpa.create(context).ccpaApplies
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
                consent = it
            )
        },
        gdpr = campaignManager.getGDPRConsent().getOrNull()?.let {
            SPGDPRConsent(
                consent = it
            )
        }
    )
}

internal fun createStorage(context: Context, spConfig: SpConfig): CampaignManager {
    val dataStorageGdpr = fetchOrStore(DataStorageGdpr::class.java) { DataStorageGdpr.create(context) }
    val dataStorageCcpa = fetchOrStore(DataStorageCcpa::class.java) { DataStorageCcpa.create(context) }
    val dataStorage = fetchOrStore(DataStorage::class.java) { DataStorage.create(context, dataStorageGdpr, dataStorageCcpa) }
    return fetchOrStore(CampaignManager::class.java) { CampaignManager.create(dataStorage, spConfig, MessageLanguage.ENGLISH) }
}
