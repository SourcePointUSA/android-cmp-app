@file:JvmName("SpUtils")

package com.sourcepoint.cmplibrary.util

import android.content.Context
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.data.network.model.SPCCPAConsent
import com.sourcepoint.cmplibrary.data.network.model.SPGDPRConsent
import com.sourcepoint.cmplibrary.model.SPConsents

fun userConsents(context: Context): SPConsents {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    val dataStorageCcpa = DataStorageCcpa.create(context)
    return userConsents(dataStorageGdpr, dataStorageCcpa)
}

fun gdprApplies(context: Context): Boolean {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    return dataStorageGdpr
        .getGdpr()
        .getOrNull()
        ?.gdprApplies
        ?: false
}

fun ccpaApplies(context: Context): Boolean {
    val dataStorageGdpr = DataStorageCcpa.create(context)
    return dataStorageGdpr
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
    dataStorageGdpr: DataStorageGdpr,
    dataStorageCcpa: DataStorageCcpa
): SPConsents {

    return SPConsents(
        ccpa = dataStorageCcpa.getCcpa().map { it.userConsent }.getOrNull()?.let {
            SPCCPAConsent(
                applies = dataStorageCcpa.ccpaApplies,
                consent = it
            )
        },
        gdpr = dataStorageGdpr.getGdpr().map { it.userConsent }.getOrNull()?.let {
            SPGDPRConsent(
                applies = dataStorageGdpr.gdprApplies,
                consent = it
            )
        }
    )
}
