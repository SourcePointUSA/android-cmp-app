package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewerFragment.Companion.LOG_ID
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewerFragment.Companion.TITLE

class JsonViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val logId = intent.getLongExtra(LOG_ID, -1L)
        val title = intent.getStringExtra(TITLE)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, JsonViewerFragment.instance(logId, title))
                .commitNow()
        }
    }
}
