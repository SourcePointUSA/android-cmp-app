package com.sourcepointmeta.metaapp.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.property.AddUpdatePropertyFragment

class MainActivityTV : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PropertyListFragmentTV())
                .commitNow()
        }
    }
}