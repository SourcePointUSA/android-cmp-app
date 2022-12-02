package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.util.check
import kotlinx.android.synthetic.main.jsonviewer_layout.*
import org.json.JSONObject

class JsonViewer4SharedPrefFragment : JsonViewerBaseFragment() {

    companion object {
        const val SP_KEY = "sp_key"
        const val SP_VALUE = "sp_value"
        fun instance(spKey: String, spValue: String) = JsonViewer4SharedPrefFragment().apply {
            arguments = Bundle().apply {
                putString(SP_KEY, spKey)
                putString(SP_VALUE, spValue)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.jsonviewer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        log_title.text = arguments?.getString(SP_KEY)

        tool_bar.run {
            title = "${BuildConfig.VERSION_NAME} - ${getString(R.string.json_analyzer_title)}"
            setNavigationOnClickListener { activity?.finish() }
        }
        val content = arguments?.getString(SP_VALUE)
        check { JSONObject(content) }
            .getOrNull()
            ?.let { rv_json.bindJson(it) }
            ?: run {
                sp_content.visibility = View.VISIBLE
                sp_content.text = content
                rv_json.visibility = View.GONE
            }
    }
}
