package com.sourcepointmeta.metaapp.ui.eventlogs

import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.databinding.LogFragmentLayoutBinding
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.BaseState.StateSharingLogs
import com.sourcepointmeta.metaapp.ui.component.LogItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogFragment : Fragment() {

    companion object {
        const val PROPERTY_NAME = "property_name"
        fun instance(propertyName: String) = LogFragment().apply {
            arguments = Bundle().apply {
                putString(PROPERTY_NAME, propertyName)
            }
        }
    }

    val propertyName: String
        get() = arguments?.getString(PROPERTY_NAME) ?: ""

    private val config: SpConfig by lazy {
        arguments?.getString(PROPERTY_NAME)
            ?.let { viewModel.getConfig(it).getOrNull() }
            ?: throw RuntimeException("extra property_name param is null!!!")
    }

    var logClickListener: ((logId: LogItem) -> Unit)? = null
    private val adapter by lazy { LogAdapter() }
    private val viewModel by viewModel<LogViewModel>()
    private lateinit var binding: LogFragmentLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.resetDataByProperty(propertyName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogFragmentLayoutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logList.layoutManager = GridLayoutManager(context, 1)
        binding.logList.adapter = adapter
        adapter.itemClickListener = { logClickListener?.invoke(it) }
        binding.logList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        viewModel.liveDataLog.observe(viewLifecycleOwner) {
            if (it.type != "INFO") {
                adapter.addItem(it)
                binding.logList.scrollToPosition(0)
            }
        }
        viewModel.liveData.observe(viewLifecycleOwner, ::stateHandler)
    }

    fun shareLogs() = viewModel.fetchLogs(propertyName, adapter.selectedIds)

    private fun stateHandler(state: BaseState) {
        when (state) {
            is StateSharingLogs -> sendEmail(state.stringifyJson)
            else -> { /* nothing */
            }
        }
    }

    fun sendEmail(stringifyJson: String) {
        val uri: Uri = FileProvider.getUriForFile(
            requireContext(),
            requireContext().applicationContext.packageName.toString() + ".provider",
            requireContext().createFileWithContent(config.propertyName, stringifyJson)
        )
        activity?.composeEmail(
            config = config,
            text = "Log",
            attachment = uri
        )
    }

    fun clearLog() {
        adapter.deleteAllItems()
    }

    fun clearSp() {
        clearLog()
        PreferenceManager.getDefaultSharedPreferences(requireContext().applicationContext).edit().clear().apply()
    }
}
