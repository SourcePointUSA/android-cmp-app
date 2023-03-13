package com.sourcepointmeta.metaapp.tv.properties

import android.os.Bundle
import android.view.View
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.* //ktlint-disable
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.createNewProperty
import com.sourcepointmeta.metaapp.tv.initEntranceTransition
import com.sourcepointmeta.metaapp.tv.lastIndex
import com.sourcepointmeta.metaapp.tv.showPropertyDetail
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.property_list_title.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class PropertyListFragmentTv : VerticalGridSupportFragment(), OnItemViewClickedListener {

    companion object {
        const val COLUMNS = 3
        const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    }

    private val viewModel: PropertyListViewModel by viewModel()
    private val presenterAdapter by lazy { ArrayObjectAdapter(PropertyViewPresenter(requireActivity())) }
    private val localGridPresenter by lazy { VerticalGridPresenter(ZOOM_FACTOR).apply { numberOfColumns = COLUMNS } }
    var deleteAllListener: ((View) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gridPresenter = localGridPresenter
        adapter = presenterAdapter
        onItemViewClickedListener = this
        prepareEntranceTransition()
        initEntranceTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        remove_all_btn?.setOnClickListener(deleteAllListener)
        add_property_button?.setOnClickListener { requireContext().createNewProperty() }
        title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"
        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is BaseState.StatePropertyList -> successState(it)
//                is BaseState.StateError -> errorState(it)
//                is BaseState.StateProperty -> updateProperty(it)
//                is BaseState.StateLoading -> savingProperty(it.propertyName, it.loading)
//                is BaseState.StateVersion -> showVersionPopup(it.version)
            }
        }
        viewModel.addDefaultTvProperties()
    }

    private fun successState(state: BaseState.StatePropertyList) {
        state.propertyList
            .map { p -> p.toPropertyDTO() }
            .let {
                presenterAdapter.clear()
                presenterAdapter.addAll(0, it)
                if (state.selectLast) {
                    setSelectedPosition(presenterAdapter.lastIndex())
                } else {
                    setSelectedPosition(0)
                }
            }
    }

    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        val propDto = item as? PropertyDTO ?: throw RuntimeException("The item must be a PropertyDTO type!!!")
        requireContext().showPropertyDetail(propDto.propertyName)
    }

    fun refreshData(focusOnLastElem: Boolean = false) {
        viewModel.fetchPropertyList(focusOnLastElem)
    }
}
