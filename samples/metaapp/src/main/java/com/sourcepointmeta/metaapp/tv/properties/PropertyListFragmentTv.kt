package com.sourcepointmeta.metaapp.tv.properties

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.* //ktlint-disable
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.addPlusBtn
import com.sourcepointmeta.metaapp.tv.createNewProperty
import com.sourcepointmeta.metaapp.tv.initEntranceTransition
import com.sourcepointmeta.metaapp.tv.showPropertyDetail
import com.sourcepointmeta.metaapp.ui.BaseState
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.plus_btn.* //ktlint-disable
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
        (view as? FrameLayout)?.addPlusBtn()
        add_property_btn.setOnClickListener { requireContext().createNewProperty() }
        removeAllBtn?.setOnClickListener(){ Toast.makeText(context, "Remove All button clicked", Toast.LENGTH_SHORT).show() }
        addBtn?.setOnClickListener(){ Toast.makeText(context, "Add button clicked", Toast.LENGTH_SHORT).show() }
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
        viewModel.fetchPropertyList()
    }

    private fun successState(it: BaseState.StatePropertyList) {
        it.propertyList
            .map { p -> p.toPropertyDTO() }
            .let {
                presenterAdapter.clear()
                presenterAdapter.addAll(0, it)
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

    fun refreshData() {
        viewModel.fetchPropertyList()
    }
}
