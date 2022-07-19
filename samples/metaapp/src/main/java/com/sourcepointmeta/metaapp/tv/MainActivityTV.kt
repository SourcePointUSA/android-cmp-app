package com.sourcepointmeta.metaapp.tv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListFragment

class MainActivityTV : AppCompatActivity() {

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