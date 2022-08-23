package com.sourcepointmeta.metaapp.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.replaceFragment
import com.sourcepointmeta.metaapp.tv.properties.PropertyListFragmentTv

class MainActivityTV : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        savedInstanceState ?: replaceFragment(R.id.container, PropertyListFragmentTv())
    }
}
