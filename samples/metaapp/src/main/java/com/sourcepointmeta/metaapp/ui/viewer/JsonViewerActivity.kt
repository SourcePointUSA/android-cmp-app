package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4LogFragment.Companion.LOG_ID
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4LogFragment.Companion.TITLE
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4SharedPrefFragment.Companion.SP_KEY
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4SharedPrefFragment.Companion.SP_VALUE

class JsonViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val logId = intent.getLongExtra(LOG_ID, -1L)
        val title = intent.getStringExtra(TITLE) ?: ""

        val spKey = intent.getStringExtra(SP_KEY)
        val spValue = intent.getStringExtra(SP_VALUE)

        if (savedInstanceState == null) {
            val fr = getViewerFragment(logId, title, spKey, spValue)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fr)
                .commitNow()
        }
    }

    private fun getViewerFragment(
        logId: Long,
        title: String,
        spKey: String?,
        spValue: String?
    ): JsonViewerBaseFragment {
        return if (spKey != null && spValue != null) JsonViewer4SharedPrefFragment.instance(spKey, spValue)
        else JsonViewer4LogFragment.instance(logId, title)
    }
}
