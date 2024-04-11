package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.databinding.ActivityLinkBinding

class LinkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLinkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.run {
            title = "${BuildConfig.VERSION_NAME} - Deep Link"
            setNavigationOnClickListener { onBackPressed() }
        }
    }
}
