package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import kotlinx.android.synthetic.main.activity_demo.*

class LinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link)
        tool_bar.run {
            title = "${BuildConfig.VERSION_NAME} - Deep Link"
            setNavigationOnClickListener { onBackPressed() }
        }
    }
}
