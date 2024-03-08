@file:JvmName("UnityUtils")

package com.sourcepoint.cmplibrary.unity3d

fun throwableToException(throwable: Throwable) {
    throw java.lang.Exception(throwable)
}

fun <T> arrayToList(array: Array<T>): List<T> {
    return array.toList()
}

fun <T> arrayToSet(array: Array<T>): Set<T> {
    return array.toSet()
}