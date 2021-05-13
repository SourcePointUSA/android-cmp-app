@file:JvmName("UnityUtils")

package com.sourcepoint.cmplibrary.unity3d

import com.sourcepoint.cmplibrary.SpConsentLib
import com.sourcepoint.cmplibrary.model.exposed.toJsonObject

fun throwableToException(throwable: Throwable) {
    throw java.lang.Exception(throwable)
}

fun <T> arrayToList(array: Array<T>): List<T> {
    return array.toList()
}

fun callCustomConsentGDPR(
    spLib: SpConsentLib,
    vendors: Array<String>,
    categories: Array<String>,
    legIntCategories: Array<String>,
    successCallback: UnityCustomConsentGDPRProxy
) {
    val vendorsList = arrayToList(vendors)
    val categoriesList = arrayToList(categories)
    val legIntCategoriesList = arrayToList(legIntCategories)
    spLib.customConsentGDPR(
        vendorsList, categoriesList, legIntCategoriesList,
        success = { spCustomConsents ->
            println("Kotlin-side custom consent: [$spCustomConsents]")
            if (spCustomConsents != null) {
                val json = spCustomConsents.toJsonObject().toString()
                successCallback.transferCustomConsentToUnitySide(json)
            } else {
                successCallback.transferCustomConsentToUnitySide(spCustomConsents)
            }
        }
    )
}
