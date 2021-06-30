package com.sourcepointmeta.metaapp.ui.viewer

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.BaseState
import kotlinx.android.synthetic.main.activity_demo.*
import kotlinx.android.synthetic.main.add_property_fragment.*
import kotlinx.android.synthetic.main.jsonviewer_layout.*
import kotlinx.android.synthetic.main.jsonviewer_layout.tool_bar
import org.koin.androidx.viewmodel.ext.android.viewModel

class JsonViewerFragment : Fragment() {

    private val viewModel by viewModel<JsonViewerViewModel>()

    val colorJsonKey: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonKey, this, true) }
            .data
    }

    val colorJsonValueText: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueText, this, true) }
            .data
    }

    val colorJsonValueNumber: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueNumber, this, true) }
            .data
    }

    val colorJsonValueUrl: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueUrl, this, true) }
            .data
    }

    val colorJsonValueNull: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueNull, this, true) }
            .data
    }

    val colorJsonValueBraces: Int by lazy {
        TypedValue().apply { requireContext().theme.resolveAttribute(R.attr.colorJsonValueBraces, this, true) }
            .data
    }

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
        log_title.text = arguments?.getString(TITLE)

        tool_bar.run {
            title = "${getString(R.string.json_analyzer_title)} - ${BuildConfig.VERSION_NAME}"
            setNavigationOnClickListener { activity?.finish() }
        }

        rv_json.run {
            setKeyColor(colorJsonKey)
            setValueTextColor(colorJsonValueText)
            setValueNumberColor(colorJsonValueNumber)
            setValueUrlColor(colorJsonValueUrl)
            setValueNullColor(colorJsonValueNull)
            setBracesColor(colorJsonValueBraces)
            setTextSize(16.toFloat())
        }
    }

    private fun stateHandler(state: BaseState) {
        (state as? BaseState.StateJson)?.let { rv_json.bindJson(it.json) }
    }
}
