package com.sourcepointmeta.metaapp.tv.properties

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.leanback.app.VerticalGridSupportFragment
import androidx.leanback.widget.* //ktlint-disable
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.addPlusBtn
import com.sourcepointmeta.metaapp.tv.createNewProperty
import com.sourcepointmeta.metaapp.tv.detail.defaultProperty
import com.sourcepointmeta.metaapp.tv.initEntranceTransition
import com.sourcepointmeta.metaapp.tv.showPropertyDetail
import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import kotlinx.android.synthetic.main.plus_btn.* //ktlint-disable

class PropertyListFragmentTv : VerticalGridSupportFragment(), OnItemViewClickedListener {

    companion object {
        const val COLUMNS = 3
        const val ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM
    }

    private val presenterAdapter by lazy { ArrayObjectAdapter(PropertyViewPresenter(requireActivity())) }
    private val localGridPresenter by lazy { VerticalGridPresenter(ZOOM_FACTOR).apply { numberOfColumns = COLUMNS } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"
        gridPresenter = localGridPresenter
        adapter = presenterAdapter
        presenterAdapter.addAll(0, dtoList)
        onItemViewClickedListener = this
        prepareEntranceTransition()
        initEntranceTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view as? FrameLayout)?.addPlusBtn()
        add_property_btn.setOnClickListener { requireContext().createNewProperty() }
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

    fun refreshData(){
        // TO DO
    }
}

val dtoList = (1..30).fold(mutableListOf<PropertyDTO>()) { acc, _ ->
    acc.apply { add(defaultProperty.toPropertyDTO()) }
}
