package com.sourcepointmeta.metaapp.tv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.tv.properties.PropertyListFragmentTv

class MainActivityTV : FragmentActivity() {

    val fragment by lazy { PropertyListFragmentTv() }
    private val br by lazy { MyBroadcastReceiver() }

    companion object {
        const val REFRESH_ACTION = "com.metaapp.broadcast.REFRESH"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        savedInstanceState ?: replaceFragment(R.id.container, fragment)
        // receiver to update the main screen
        registerReceiver(br, IntentFilter().apply { addAction(REFRESH_ACTION) })
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            fragment.refreshData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
    }
}
