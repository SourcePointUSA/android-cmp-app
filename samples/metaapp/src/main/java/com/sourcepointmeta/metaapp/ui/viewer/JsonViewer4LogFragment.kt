package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.databinding.JsonviewerLayoutBinding
import com.sourcepointmeta.metaapp.ui.BaseState
import org.koin.androidx.viewmodel.ext.android.viewModel

class JsonViewer4LogFragment : JsonViewerBaseFragment() {

    private val viewModel by viewModel<JsonViewerViewModel>()
    private lateinit var binding: JsonviewerLayoutBinding

    companion object {
        const val LOG_ID = "log_id"
        const val TITLE = "log_title"
        fun instance(logId: Long, title: String) = JsonViewer4LogFragment().apply {
            arguments = Bundle().apply {
                putLong(LOG_ID, logId)
                putString(TITLE, title)
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
        viewModel.liveData.observe(viewLifecycleOwner, ::stateHandler)
        arguments?.getLong(LOG_ID)?.let { viewModel.fetchJson(it) }
        binding.logTitle.text = arguments?.getString(TITLE)

        binding.toolBar?.run {
            title = "${BuildConfig.VERSION_NAME} - ${getString(R.string.json_analyzer_title)}"
            setNavigationOnClickListener { activity?.finish() }
        }
    }

    private fun stateHandler(state: BaseState) {
        (state as? BaseState.StateJson)?.let { binding.rvJson.bindJson(it.json) }
    }
}
