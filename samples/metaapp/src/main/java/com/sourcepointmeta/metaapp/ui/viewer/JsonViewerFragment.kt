package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.eventlogs.LogFragment
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.jsonviewer_layout.*
import kotlinx.android.synthetic.main.jsonviewer_layout.tool_bar
import org.koin.androidx.viewmodel.ext.android.viewModel

class JsonViewerFragment : Fragment() {

    private val viewModel by viewModel<JsonViewerViewModel>()

    companion object {
        const val LOG_ID = "log_id"
        const val TITLE = "log_title"
        fun instance(logId: Long, title: String) = JsonViewerFragment().apply {
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
        return inflater.inflate(R.layout.jsonviewer_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner, ::stateHandler)
        arguments?.getLong(LOG_ID)?.let { viewModel.fetchJson(it) }
        tool_bar.setNavigationOnClickListener { activity?.finish() }
        log_title.text = arguments?.getString(TITLE)

    }

    private fun stateHandler(state: BaseState) {
        (state as? BaseState.StateJson)?.let { rv_json.bindJson(it.json) }
    }
}
