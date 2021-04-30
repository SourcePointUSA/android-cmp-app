@file:JvmName("UnityUtils")

package com.sourcepoint.cmplibrary.util

import com.sourcepoint.cmplibrary.model.exposed.TargetingParam

fun targetingParamArrayToList(array: Array<TargetingParam>): List<TargetingParam> {
    return array.toList()
}

fun throwableToException(throwable: Throwable) {
    throw java.lang.Exception(throwable)
}
