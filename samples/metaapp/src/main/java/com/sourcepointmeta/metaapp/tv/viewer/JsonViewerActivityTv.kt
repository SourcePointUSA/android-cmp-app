package com.sourcepointmeta.metaapp.tv.viewer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4LogFragment.Companion.LOG_ID
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4LogFragment.Companion.TITLE

class JsonViewerActivityTv : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val logId = intent.getLongExtra(LOG_ID, -1L)
        val title = intent.getStringExtra(TITLE) ?: DEFAULT_TITLE
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, JsonViewerFragmentTv.instance(logId, title))
                .commitNow()
        }
    }

    companion object {
        private const val DEFAULT_TITLE = ""
    }
}
