package com.sourcepointmeta.metaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.data.localdatasource.LocalDataSource
import com.sourcepointmeta.metaapp.ui.component.LogAdapter
import com.sourcepointmeta.metaapp.ui.component.toLogItem
import kotlinx.android.synthetic.main.log_fragment_layout.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogFragment : Fragment() {

    companion object {
        fun instance(propertyName: String) = LogFragment().apply {
            arguments = Bundle().apply {
                putString("property_name", propertyName)
            }
        }
    }

    private val adapter by lazy { LogAdapter() }

    private val viewModel by viewModel<LogViewModel>()

    private val propertyName by lazy {
        arguments?.getString("property_name") ?: throw RuntimeException("Property name not set!!!")
    }
    private val dataSource by inject<LocalDataSource>()
    private val config: SpConfig by lazy {
        dataSource.getSPConfig(propertyName).getOrNull() ?: throw RuntimeException("Property name not set!!!")
    }

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
        log_list.addItemDecoration(DividerItemDecoration(log_list.context, DividerItemDecoration.VERTICAL))
//        viewModel.liveData.observe(viewLifecycleOwner, ::stateHandler)
//        viewModel.fetchLogs(propertyName)
        viewModel.liveDataLog.observe(viewLifecycleOwner) {
            if (it.type != "INFO") {
                adapter.addItem(it.toLogItem())
                log_list.scrollToPosition(0)
            }
        }
    }

    private fun stateHandler(state: BaseState) {
        (state as? BaseState.StateLogList)?.let {
            val logs = it.propertyList.map { e -> e.toLogItem() }
            adapter.addItems(logs)
        }
    }
}
