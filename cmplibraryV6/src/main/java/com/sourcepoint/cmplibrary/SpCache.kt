package com.sourcepoint.cmplibrary

import com.sourcepoint.cmplibrary.data.network.converter.fail

internal interface SpCache {
    fun <T : Any> fetchOrStore(modelClass: Class<T>, block: () -> T): T
    fun <T : Any> fetch(modelClass: Class<T>): T?
    companion object
}

internal object SpCacheObjet : SpCache {
    private val cache: MutableMap<Class<out Any>, Any> = mutableMapOf()
    override fun <T : Any> fetchOrStore(modelClass: Class<T>, block: () -> T): T {
        return (cache.getOrPut(modelClass, block) as T)
    }

    override fun <T : Any> fetch(modelClass: Class<T>): T? {
        return (cache[modelClass] as? T) ?: fail("$modelClass has not been created!!!")
    }
}
