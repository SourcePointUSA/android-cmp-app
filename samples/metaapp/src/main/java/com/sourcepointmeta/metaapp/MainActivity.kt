package com.sourcepointmeta.metaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.ui.PropertyFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PropertyFragment.newInstance())
                .commitNow()
        }
    }
}
