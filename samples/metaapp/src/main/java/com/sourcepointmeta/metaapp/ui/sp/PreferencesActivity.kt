package com.sourcepointmeta.metaapp.ui.sp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.databinding.PrefActivityBinding
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewer4SharedPrefFragment
import com.sourcepointmeta.metaapp.ui.viewer.JsonViewerActivity

class PreferencesActivity : AppCompatActivity() {

    private val spPref by lazy { SpFragment.instance() }
    private lateinit var binding: PrefActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PrefActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frag_container, spPref)
                .commitNow()
        }

        binding.toolBar.run {
            title = "${BuildConfig.VERSION_NAME} - Preferences"
            setNavigationOnClickListener { onBackPressed() }
        }

        spPref.spItemClickListener = { key, value ->
            intent.putExtra("run_demo", false)
            val intent = Intent(baseContext, JsonViewerActivity::class.java)
            intent.putExtra(JsonViewer4SharedPrefFragment.SP_KEY, key)
            intent.putExtra(JsonViewer4SharedPrefFragment.SP_VALUE, value)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
