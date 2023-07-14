package com.sourcepoint.app.v6.pm_test

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.sourcepoint.app.v6.R
import com.sourcepoint.cmplibrary.util.clearAllData
import io.reactivex.disposables.CompositeDisposable

class PmTestMainActivity : AppCompatActivity() {

    private val disposables = CompositeDisposable()

    private var counter = 0

    private val root by lazy { findViewById<FrameLayout>(R.id.source_point_root) }
    private val reloadButton by lazy { findViewById<Button>(R.id.button_reload) }

    private var platform: ConsentManagementPlatform? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pm_test_activity_main)

        reloadButton.setOnClickListener {
            counter++
            Log.i("DIA-1716", "=== reload ===")
            Log.d("DIA-1716", "counter = $counter")
            loadFLM()
        }

        loadFLM()
    }

    private fun loadFLM() {
        // for first layer to be loaded each time
        clearAllData(applicationContext)

        platform?.dispose()
        root.removeAllViews()
        val new =
            SourcePointConsentManagementPlatformImpl(
                sourcePointConfig = SourcePointConfig.hardcoded(),
                activity = this
            )

        // only open first layer/load message
        new.openFirstLayer({})
        disposables.clear()

        platform = new

        disposables.add(
            new.events.subscribe { event ->

                when (event) {
                    is ConsentManagementPlatform.Event.UiEvent -> {
                        when (event) {
                            is ConsentManagementPlatform.Event.UiEvent.ViewReady ->
                                root.addView(event.view)
                            is ConsentManagementPlatform.Event.UiEvent.ViewFinished ->
                                root.removeView(event.view)
                        }

                    }
                    is ConsentManagementPlatform.Event.Finished -> {
                        platform?.dispose()
                    }
                }
            }
        )
    }
}