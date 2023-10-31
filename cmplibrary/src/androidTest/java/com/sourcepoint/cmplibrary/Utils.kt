package com.sourcepoint.cmplibrary

import android.content.Context
import android.preference.PreferenceManager

class Utils {
    companion object {

        fun <E> check(block: () -> E): E? {
            return try {
                block.invoke()
            } catch (e: Exception) {
                null
            }
        }

        fun Context.storeTestDataObj(list: List<Pair<String, Any?>>) {
            val sp = PreferenceManager.getDefaultSharedPreferences(this)
            val spEditor = sp.edit()
            list.forEach {
                check { it.second as? String }?.let { v -> spEditor.putString(it.first, v) }
                check { it.second as? Boolean }?.let { v -> spEditor.putBoolean(it.first, v) }
                check { it.second as? Int }?.let { v -> spEditor.putInt(it.first, v) }
            }
            spEditor.apply()
        }

        fun Context.spEntries(): Map<String, Any?> {
            return PreferenceManager.getDefaultSharedPreferences(this).all
        }
    }
}
