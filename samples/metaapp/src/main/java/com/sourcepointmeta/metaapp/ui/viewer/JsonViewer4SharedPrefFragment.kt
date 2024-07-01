package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.databinding.JsonviewerLayoutBinding
import com.sourcepointmeta.metaapp.util.check
import org.json.JSONObject

class JsonViewer4SharedPrefFragment : JsonViewerBaseFragment() {

    private lateinit var binding: JsonviewerLayoutBinding

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
        binding = JsonviewerLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logTitle.text = arguments?.getString(SP_KEY)

        binding.toolBar?.run {
            title = "${BuildConfig.VERSION_NAME} - ${getString(R.string.json_analyzer_title)}"
            setNavigationOnClickListener { activity?.finish() }
        }
        val content = arguments?.getString(SP_VALUE)
        check { JSONObject(content) }
            .getOrNull()
            ?.let { binding.rvJson.bindJson(it) }
            ?: run {
                binding.spContent?.visibility = View.VISIBLE
                binding.spContent?.text = content
                binding.rvJson.visibility = View.GONE
            }
    }
}
