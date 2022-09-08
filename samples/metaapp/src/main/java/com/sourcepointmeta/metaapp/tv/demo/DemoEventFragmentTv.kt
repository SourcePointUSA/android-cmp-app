package com.sourcepointmeta.metaapp.tv.demo

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.content.FileProvider
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.* //ktlint-disable
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepoint.cmplibrary.model.exposed.SpConfig
import com.sourcepointmeta.metaapp.core.getOrNull
import com.sourcepointmeta.metaapp.tv.bounceEventAndSelectFirstElement
import com.sourcepointmeta.metaapp.tv.initEntranceTransition
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.LogItem
import com.sourcepointmeta.metaapp.ui.eventlogs.LogViewModel
import com.sourcepointmeta.metaapp.ui.eventlogs.composeEmail
import com.sourcepointmeta.metaapp.ui.eventlogs.createFileWithContent
import kotlinx.android.synthetic.main.demo_header.* //ktlint-disable
import kotlinx.coroutines.channels.BroadcastChannel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DemoEventFragmentTv : VerticalGridSupportFragment(), OnItemViewClickedListener {

    companion object {
        const val COLUMNS = 1
        const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_SMALL
        const val PROPERTY_NAME = "property_name"
        fun instance(propertyName: String) = DemoEventFragmentTv().apply {
            arguments = Bundle().apply {
                putString(PROPERTY_NAME, propertyName)
            }
        }
    }

    val channel = BroadcastChannel<Int>(1)

    var pmListener: ((CampaignType) -> Unit)? = null
    var flmListener: (() -> Unit)? = null
    var logClickListener: ((logId: LogItem) -> Unit)? = null

    private val viewModel by viewModel<LogViewModel>()

    private val propertyName by lazy {
        arguments?.getString(PROPERTY_NAME) ?: throw RuntimeException("Property name not set!!!")
    }

    private val config: SpConfig by lazy {
        arguments?.getString(PROPERTY_NAME)
            ?.let { viewModel.getConfig(it).getOrNull() }
            ?: throw RuntimeException("extra property_name param is null!!!")
    }

    private val demoViewPresenter by lazy { DemoViewPresenter(requireActivity()) }
    private val presenterAdapter by lazy { ArrayObjectAdapter(demoViewPresenter) }
    private val localGridPresenter by lazy { VerticalGridPresenter(ZOOM_FACTOR).apply { numberOfColumns = COLUMNS } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        gridPresenter = localGridPresenter
        adapter = presenterAdapter
        onItemViewClickedListener = this
        prepareEntranceTransition()
        initEntranceTransition()
        viewModel.resetDataByProperty(propertyName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gdpr_pm.setOnClickListener { pmListener?.invoke(CampaignType.GDPR) }
        refresh_flm.setOnClickListener { flmListener?.invoke() }
        viewModel.liveDataLog.observe(viewLifecycleOwner) {
            if (it.type != "INFO") {
                presenterAdapter.add(0, it)
                bounceEventAndSelectFirstElement()
            }
        }
        viewModel.liveData.observe(viewLifecycleOwner, ::stateHandler)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenterAdapter.clear()
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val propDto = item as? LogItem ?: throw RuntimeException("The item must be a PropertyDTO type!!!")
        logClickListener?.invoke(propDto)
    }

    private fun stateHandler(state: BaseState) {
        when (state) {
            is BaseState.StateSharingLogs -> {
                val uri: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName.toString() + ".provider",
                    requireContext().createFileWithContent(config.propertyName, state.stringifyJson)
                )
                activity?.composeEmail(
                    config = config,
                    text = "Log",
                    attachment = uri
                )
            }
            else -> { /* nothing */
            }
        }
    }
}
