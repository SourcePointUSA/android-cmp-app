package com.sourcepointmeta.metaapp.tv

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.tv.properties.PropertyListFragmentTv

class MainActivityTV : FragmentActivity() {

    val fragment by lazy { PropertyListFragmentTv() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        savedInstanceState ?: replaceFragment(R.id.container, fragment)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({fragment.refreshData()}, 2000)

    }
}
