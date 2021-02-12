@file:JvmName("SpUtils")

package com.sourcepoint.cmplibrary

import android.content.Context
import com.sourcepoint.cmplibrary.data.local.DataStorage
import com.sourcepoint.cmplibrary.data.local.DataStorageCcpa
import com.sourcepoint.cmplibrary.data.local.DataStorageGdpr
import com.sourcepoint.cmplibrary.data.local.create
import com.sourcepoint.cmplibrary.util.getOrNull
import com.sourcepoint.cmplibrary.util.map

fun userConsents(context: Context): SPConsents {
    val dataStorageGdpr = DataStorageGdpr.create(context)
    val dataStorageCcpa = DataStorageCcpa.create(context)
    return SPConsents(
        ccpa = dataStorageCcpa.getCcpa().map { it.userConsent }.getOrNull(),
        gdpr = dataStorageGdpr.getGdpr().map { it.userConsent }.getOrNull()
    )
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
