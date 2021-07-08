package com.sourcepointmeta.metaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
