package com.sourcepointmeta.metaapp.ui.eventlogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.LogAdapter
import com.sourcepointmeta.metaapp.ui.component.LogItem
import com.sourcepointmeta.metaapp.ui.component.toLogItem
import kotlinx.android.synthetic.main.log_fragment_layout.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogFragment : Fragment() {

    companion object {
        fun instance(propertyName: String) = LogFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }

    var logClickListener: ((logId: LogItem) -> Unit)? = null
    private val adapter by lazy { LogAdapter() }
    private val viewModel by viewModel<LogViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.log_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log_list.layoutManager = GridLayoutManager(context, 1)
        log_list.adapter = adapter
        adapter.itemClickListener = { logClickListener?.invoke(it) }
        log_list.addItemDecoration(DividerItemDecoration(log_list.context, DividerItemDecoration.VERTICAL))
        viewModel.liveDataLog.observe(viewLifecycleOwner) {
            if (it.type != "INFO") {
                adapter.addItem(it)
                log_list.scrollToPosition(0)
            }
        }
    }
}
